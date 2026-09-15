import { ChangeDetectorRef, Component, DestroyRef, HostListener, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';

import { CorteService } from '../../services/corte/corte-service';
import { LoteService } from '../../services/lote/lote-service';
import { EnfestadorService } from '../../services/enfestador/enfestador-service';
import { CortadorService } from '../../services/cortador/cortador-service';
import { RelatorioService } from '../../services/relatorio/relatorio-service';

import { CorteResponse } from '../../interfaces/corte/CorteResponse';
import { CorteRequest } from '../../interfaces/corte/CorteRequest';
import { CorteUpdateRequest } from '../../interfaces/corte/CorteUpdate';
import { CortadorResponse } from '../../interfaces/cortador/CortadorResponse';
import { EnfestadorResponse } from '../../interfaces/enfestador/EnfestadorResponse';

type StatusCorte = 'pendente' | 'enfestado' | 'cortado';
type TipoPagina = 'Geral' | 'Pendentes' | 'Enfestados' | 'Cortados';
type TipoLote = 'atual' | 'novo';

const MESES = [
  'JANEIRO', 'FEVEREIRO', 'MARÇO', 'ABRIL', 'MAIO', 'JUNHO',
  'JULHO', 'AGOSTO', 'SETEMBRO', 'OUTUBRO', 'NOVEMBRO', 'DEZEMBRO'
] as const;

const STATUS_POR_PAGINA: Partial<Record<TipoPagina, StatusCorte>> = {
  Pendentes: 'pendente',
  Enfestados: 'enfestado',
  Cortados: 'cortado'
};

const corteRequestVazio = (): CorteRequest => ({
  nomeModelo: '',
  quantidadeTotal: null,
  dataDeRegistro: new Date().toISOString().split('T')[0],
  observacao: ''
});

@Component({
  selector: 'app-content-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './content-component.html',
  styleUrl: './content-component.scss'
})
export class ContentComponent implements OnInit {

  // ---------------------------------------------------------------------
  // Injeção de dependências
  // ---------------------------------------------------------------------
  private readonly route = inject(ActivatedRoute);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);

  private readonly corteService = inject(CorteService);
  private readonly loteService = inject(LoteService);
  private readonly enfestadorService = inject(EnfestadorService);
  private readonly cortadorService = inject(CortadorService);
  private readonly relatorioService = inject(RelatorioService);

  // ---------------------------------------------------------------------
  // Constantes de exibição
  // ---------------------------------------------------------------------
  readonly meses = MESES;
  readonly anoAtual = new Date().getFullYear();
  readonly mesAtual = MESES[new Date().getMonth()];

  // ---------------------------------------------------------------------
  // Estado da listagem principal
  // ---------------------------------------------------------------------
  pageSelected: TipoPagina = 'Geral';
  cortesDoMes: CorteResponse[] = [];
  relatorioDeCortes = 0;
  loteAtual = '';
  nomeParaBuscar = '';
  telaCarregandoModal = false;

  // ---------------------------------------------------------------------
  // Dados de apoio (selects de cortador/enfestador)
  // ---------------------------------------------------------------------
  cortadores: CortadorResponse[] = [];
  enfestadores: EnfestadorResponse[] = [];

  // ---------------------------------------------------------------------
  // Estado de seleção / edição de um corte
  // ---------------------------------------------------------------------
  corteSelecionado: CorteResponse | null = null;
  corteEdicao: CorteResponse | null = null;
  corteAdicionadoRecentemente: CorteResponse | null = null;
  idEnfestador: number | null = null;
  idCortador: number | null = null;

  // ---------------------------------------------------------------------
  // Estado do card de opções (menu de ações do item)
  // ---------------------------------------------------------------------
  corteComOpcoesAbertas: CorteResponse | null = null;
  opcoesCardVisiveis = false;

  // ---------------------------------------------------------------------
  // Estado de criação de um novo corte
  // ---------------------------------------------------------------------
  modoCriacao = false;
  tipoLote: TipoLote = 'atual';
  novoCorte: CorteRequest = corteRequestVazio();

  // ---------------------------------------------------------------------
  // Mapeamento de ações ao pressionar teclas
  // ---------------------------------------------------------------------
  @HostListener('document:keydown.escape')
  onEscape() {
    this.corteSelecionado = null;
  }

  // ---------------------------------------------------------------------
  // Validação
  // ---------------------------------------------------------------------
  erros: Record<string, string> = {};

  // =======================================================================
  // Ciclo de vida
  // =======================================================================

  ngOnInit(): void {
    this.route.params
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(params => {
        this.pageSelected = (params['tipo'] as TipoPagina) || 'Geral';
        this.nomeParaBuscar = '';
        this.buscarCortesDoMesPorStatus();
      });

    this.buscarQuantidadeDeCortesDoMes();
    this.buscarLoteAtual();
    this.buscarCortadores();
    this.buscarEnfestadores();
  }

  // =======================================================================
  // Busca de dados
  // =======================================================================

  buscarQuantidadeDeCortesDoMes(): void {
    const { ano, mes } = this.obterAnoEMesAtual();

    this.relatorioService.buscarQuantidadeDeCortesDoMes(mes, ano)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => (this.relatorioDeCortes = data.quantidade),
        error: (err) => console.error('Erro ao buscar relatório do mês:', err)
      });
  }

  buscarCortadores(): void {
    this.cortadorService.buscarCortadores()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => { this.cortadores = data; this.cdr.detectChanges(); },
        error: (err) => console.error('Erro ao buscar cortadores:', err)
      });
  }

  buscarEnfestadores(): void {
    this.enfestadorService.buscarEnfestadores()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => { this.enfestadores = data; this.cdr.detectChanges(); },
        error: (err) => console.error('Erro ao buscar enfestadores:', err)
      });
  }

  buscarLoteAtual(): void {
    this.loteService.buscarLote()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => { this.loteAtual = data.numero_lote; this.cdr.detectChanges(); },
        error: (err) => console.error('Erro ao buscar lote atual:', err)
      });
  }

  buscarCortesDoMesPorStatus(): void {
    const { ano, mes } = this.obterAnoEMesAtual();

    const request$ = this.pageSelected === 'Geral'
      ? this.corteService.buscarCortesDoMes(mes, ano)
      : this.corteService.buscarCortesDoMesPorStatus(
          STATUS_POR_PAGINA[this.pageSelected]!,
          ano,
          mes
        );

    request$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => { this.cortesDoMes = data; this.cdr.detectChanges(); },
        error: (err) => console.error('Erro ao buscar cortes do mês:', err)
      });
  }

  buscarCortePorNomeOuLote(): void {
    if (!this.nomeParaBuscar) {
      this.buscarCortesDoMesPorStatus();
      return;
    }

    const { ano, mes } = this.obterAnoEMesAtual();
    this.telaCarregandoModal = true;

    this.corteService.buscarCortePorNomeOuLote(this.nomeParaBuscar, mes, ano)
      .pipe(
        finalize(() => {
          setTimeout(() => {
            this.telaCarregandoModal = false;
            this.cdr.detectChanges();
          }, 300);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (data) => { this.cortesDoMes = data; this.cdr.detectChanges(); },
        error: (err) => console.error('Erro ao buscar corte por nome ou lote:', err)
      });
  }

  // =======================================================================
  // Seleção e edição de um corte
  // =======================================================================

  selecionarCorte(corte: CorteResponse): void {
    this.erros = {};
    this.corteSelecionado = corte;
    this.corteEdicao = {
      ...corte,
      dataDeCorte: corte.dataDeCorte ? this.converterDataParaISO(corte.dataDeCorte) : corte.dataDeCorte
    };
    this.idEnfestador = corte.enfestador?.id ?? null;
    this.idCortador = corte.cortador?.id ?? null;
    this.opcoesCardVisiveis = false;
  }

  fecharModalCorte(): void {
    this.corteSelecionado = null;
    this.corteEdicao = null;
  }

  atualizarCorte(): void {
    if (!this.corteSelecionado || !this.corteEdicao) return;

    const corteParaAtualizar = this.montarCorteUpdateRequest(
      this.corteEdicao,
      this.idCortador,
      this.idEnfestador
    );

    if (!this.validarAtualizacao(corteParaAtualizar)) return;

    this.corteService.atualizarCorte(this.corteSelecionado.id, corteParaAtualizar)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.substituirCorteNaLista(data);
          this.limparSelecaoDeEdicao();
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao atualizar corte:', err)
      });
  }

  ativarCorte(corte: CorteResponse): void {
    const corteParaAtualizar = this.montarCorteUpdateRequest(
      corte,
      corte.cortador?.id ?? null,
      corte.enfestador?.id ?? null
    );

    if (!this.validarAtualizacao(corteParaAtualizar)) return;

    this.corteService.atualizarCorte(corte.id, corteParaAtualizar)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.substituirCorteNaLista(data);
          this.limparSelecaoDeEdicao();
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao ativar corte:', err)
      });

    this.opcoesCardVisiveis = false;
  }

  private validarAtualizacao(corte: CorteUpdateRequest): boolean {
    this.erros = {};

    if (!corte.loteFormatado.trim()) {
      this.erros['loteAtt'] = 'Lote obrigatório';
    } else if (!/^[0-9/]+$/.test(corte.loteFormatado)) {
      this.erros['loteAtt'] = 'O lote deve conter apenas números e "/"';
    } else if (!corte.loteFormatado.includes('/')) {
      this.erros['loteAtt'] = 'O lote deve conter "/"';
    }

    if (!corte.nomeModelo.trim()) {
      this.erros['nomeModeloAtt'] = 'Nome do modelo obrigatório';
    }

    if (!corte.quantidadeTotal || corte.quantidadeTotal <= 0) {
      this.erros['quantidadeAtt'] = 'Quantidade obrigatória';
    }

    if (corte.idCortador != null && corte.idEnfestador == null) {
      this.erros['enfestadorAttError'] = 'Selecione um enfestador';
    }

    if (corte.dataDeCorte == null && corte.idCortador != null) {
      this.erros['dataCorteAtt'] = 'Selecione a data de corte';
    }

    return Object.keys(this.erros).length === 0;
  }

  // =======================================================================
  // Criação de um novo corte
  // =======================================================================

  abrirCriacao(): void {
    this.modoCriacao = true;
  }

  fecharCriacao(): void {
    this.modoCriacao = false;
  }

  adicionarCorte(): void {
    this.erros = {};

    if (!this.novoCorte.dataDeRegistro) {
      this.erros['dataRegistro'] = 'Data de Registro obrigatória';
    }
    if (!this.novoCorte.nomeModelo.trim()) {
      this.erros['nomeModelo'] = 'Nome do modelo obrigatório';
    }
    if (!this.novoCorte.quantidadeTotal) {
      this.erros['quantidade'] = 'Quantidade obrigatória';
    }

    if (Object.keys(this.erros).length > 0) return;

    if (this.tipoLote === 'novo') {
      this.loteService.incrementarLote()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => this.salvarCorte(),
          error: (err) => console.error('Erro ao incrementar lote:', err)
        });
    } else {
      this.salvarCorte();
    }
  }

  private salvarCorte(): void {
    this.corteService.adicionarCorte(this.novoCorte)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.corteAdicionadoRecentemente = data;
          this.novoCorte = corteRequestVazio();
          this.fecharCriacao();
          this.buscarLoteAtual();
          this.buscarQuantidadeDeCortesDoMes();
          this.buscarCortesDoMesPorStatus();
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao salvar corte:', err)
      });
  }

  // =======================================================================
  // Cancelamento e exclusão
  // =======================================================================

  cancelarCorte(corte: CorteResponse): void {
    this.corteService.cancelarCorte(corte.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          if (this.corteAdicionadoRecentemente?.id === corte.id) {
            this.corteAdicionadoRecentemente.corteStatus = 'CANCELADO';
          }
          this.buscarCortesDoMesPorStatus();
          this.limparSelecaoDeEdicao();
          this.opcoesCardVisiveis = false;
        },
        error: (err) => console.error('Erro ao cancelar corte:', err)
      });
  }

  excluirCorte(id: number): void {
    if (id === this.corteAdicionadoRecentemente?.id) {
      this.corteAdicionadoRecentemente = null;
    }

    this.corteService.excluirCorte(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.buscarCortesDoMesPorStatus();
          this.buscarQuantidadeDeCortesDoMes();
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao excluir corte:', err)
      });
  }

  // =======================================================================
  // UI: card de opções e recarregamento
  // =======================================================================

  abrirModalOpcao(corte: CorteResponse): void {
    if (this.corteComOpcoesAbertas === corte) {
      this.opcoesCardVisiveis = !this.opcoesCardVisiveis;
    } else {
      this.corteComOpcoesAbertas = corte;
      this.opcoesCardVisiveis = true;
    }
  }  

  pegarStatusParaEstilo(status: string): string {
    return status.toLowerCase();
  }

  // =======================================================================
  // Utilitários privados
  // =======================================================================

  private obterAnoEMesAtual(): { ano: number; mes: number } {
    const agora = new Date();
    return { ano: agora.getFullYear(), mes: agora.getMonth() + 1 };
  }

  /** Converte data no formato dd/mm/aaaa para aaaa-mm-dd (usado em inputs type="date"). */
  private converterDataParaISO(data: string): string {
    const [dia, mes, ano] = data.split('/');
    return `${ano}-${mes}-${dia}`;
  }

  private montarCorteUpdateRequest(
    corte: CorteResponse,
    idCortador: number | null,
    idEnfestador: number | null
  ): CorteUpdateRequest {

    
    const dataParaAtualizar = corte.dataDeCorte != null ? this.converterDataParaISO(corte.dataDeCorte) : null;

    return {
      dataDeCorte: dataParaAtualizar,
      loteFormatado: corte.loteFormatado,
      nomeModelo: corte.nomeModelo,
      quantidadeTotal: corte.quantidadeTotal,
      observacao: corte.observacao,
      idCortador: idCortador,
      idEnfestador: idEnfestador
    };
  }

  private substituirCorteNaLista(corteAtualizado: CorteResponse): void {
    if (this.corteAdicionadoRecentemente?.id === corteAtualizado.id) {
      this.corteAdicionadoRecentemente = corteAtualizado;
    }

    const index = this.cortesDoMes.findIndex(c => c.id === corteAtualizado.id);
    if (index !== -1) {
      this.cortesDoMes[index] = corteAtualizado;
    }
  }

  private limparSelecaoDeEdicao(): void {
    this.corteSelecionado = null;
    this.corteEdicao = null;
    this.idCortador = null;
    this.idEnfestador = null;
  }
}