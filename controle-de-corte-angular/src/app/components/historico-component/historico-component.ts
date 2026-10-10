import { ChangeDetectorRef, Component, DestroyRef, HostListener, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';

import { CorteService } from '../../services/corte/corte-service';
import { EnfestadorService } from '../../services/enfestador/enfestador-service';
import { CortadorService } from '../../services/cortador/cortador-service';

import { CorteResponse } from '../../interfaces/corte/CorteResponse';
import { CorteUpdateRequest } from '../../interfaces/corte/CorteUpdate';
import { CortadorResponse } from '../../interfaces/cortador/CortadorResponse';
import { EnfestadorResponse } from '../../interfaces/enfestador/EnfestadorResponse';

const NOMES_MESES = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
] as const;

type StatusCorte = 'PENDENTE' | 'ENFESTADO' | 'CORTADO' | 'CANCELADO';
type FiltroStatus = 'TODOS' | StatusCorte;

// Os valores de status espelham o enum CorteStatus do backend
const FILTROS_STATUS: { valor: FiltroStatus; rotulo: string }[] = [
  { valor: 'TODOS', rotulo: 'Todos' },
  { valor: 'PENDENTE', rotulo: 'Pendentes' },
  { valor: 'ENFESTADO', rotulo: 'Enfestados' },
  { valor: 'CORTADO', rotulo: 'Cortados' },
  { valor: 'CANCELADO', rotulo: 'Cancelados' }
];

const contagensZeradas = (): Record<FiltroStatus, number> => ({
  TODOS: 0, PENDENTE: 0, ENFESTADO: 0, CORTADO: 0, CANCELADO: 0
});

@Component({
  selector: 'app-historico-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './historico-component.html',
  styleUrl: './historico-component.scss',
})
export class HistoricoComponent implements OnInit {

  // ---------------------------------------------------------------------
  // Injeção de dependências
  // ---------------------------------------------------------------------
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);

  private readonly corteService = inject(CorteService);
  private readonly enfestadorService = inject(EnfestadorService);
  private readonly cortadorService = inject(CortadorService);

  // ---------------------------------------------------------------------
  // Estado da busca por mês
  // ---------------------------------------------------------------------
  // Último mês permitido no input (yyyy-MM). Meses futuros são bloqueados.
  readonly mesMaximo = this.getMesAtualFormatado();

  mesSelecionado = '';
  mesBuscado = '';
  erroBusca = '';
  infoResult = false;
  telaCarregandoModal = false;

  // ---------------------------------------------------------------------
  // Estado da listagem
  // ---------------------------------------------------------------------
  cortesBuscados: CorteResponse[] = [];
  cortesFiltrados: CorteResponse[] = [];

  // ---------------------------------------------------------------------
  // Filtros em tempo real (aplicados no front, sem consultar o banco)
  // ---------------------------------------------------------------------
  readonly filtrosStatus = FILTROS_STATUS;
  filtroStatus: FiltroStatus = 'TODOS';
  termoBusca = '';
  contagens: Record<FiltroStatus, number> = contagensZeradas();

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
  idEnfestador: number | null = null;
  idCortador: number | null = null;

  // ---------------------------------------------------------------------
  // Estado do card de opções (menu de ações do item)
  // ---------------------------------------------------------------------
  opcoesCard = false;
  corteAbertoOpcao: CorteResponse | null = null;

  // ---------------------------------------------------------------------
  // Mapeamento de ações ao pressionar teclas
  // ---------------------------------------------------------------------
  @HostListener('document:keydown.escape')
  onEscape() {
    this.corteSelecionado = null;
    this.opcoesCard = false;
  }

  // ---------------------------------------------------------------------
  // Validação
  // ---------------------------------------------------------------------
  erros: Record<string, string> = {};

  // =======================================================================
  // Ciclo de vida
  // =======================================================================

  ngOnInit(): void {
    this.mesSelecionado = this.mesMaximo;
    this.buscarCortadores();
    this.buscarEnfestadores();
  }

  // =======================================================================
  // Busca de dados
  // =======================================================================

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

  buscarCortes(): void {
    this.erroBusca = '';

    // O formato yyyy-MM é comparável como string
    if (this.mesSelecionado > this.mesMaximo) {
      this.erroBusca = 'Não é possível buscar meses futuros.';
      return;
    }

    const [ano, mes] = this.mesSelecionado.split('-');
    const anoFormatado = ano ? Number(ano) : null;
    const mesFormatado = mes ? Number(mes) : null;

    this.telaCarregandoModal = true;

    this.corteService.buscarCortesDoMes(mesFormatado, anoFormatado, true)
      .pipe(
        finalize(() => {
          setTimeout(() => {
            this.telaCarregandoModal = false;
            this.cdr.detectChanges();
          }, 400);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (data) => {
          if (this.mesSelecionado !== this.mesBuscado) {
            // Mês novo: começa sem os filtros da busca anterior
            this.termoBusca = '';
            this.filtroStatus = 'TODOS';
          }

          this.cortesBuscados = data;
          this.mesBuscado = this.mesSelecionado;
          this.infoResult = true;
          this.aplicarFiltros();
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao buscar cortes:', err)
      });
  }

  // =======================================================================
  // Seleção e edição de um corte
  // =======================================================================

  selecionarCorte(corte: CorteResponse): void {
    if (this.corteSelecionado?.id === corte.id) {
      this.limparSelecaoDeEdicao();
      return;
    }

    this.erros = {};
    this.corteSelecionado = corte;
    this.corteEdicao = {
      ...corte,
      dataDeCorte: this.converterDataParaISO(corte.dataDeCorte) ?? ''
    };
    this.idEnfestador = corte.enfestador?.id ?? null;
    this.idCortador = corte.cortador?.id ?? null;
    this.opcoesCard = false;
  }

  cancelarEdicao(): void {
    this.limparSelecaoDeEdicao();
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
        next: () => {
          this.limparSelecaoDeEdicao();
          this.buscarCortes();
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

    this.opcoesCard = false;
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
  // Cancelamento e exclusão
  // =======================================================================

  cancelarCorte(corte: CorteResponse): void {
    this.corteService.cancelarCorte(corte.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.buscarCortes();
          this.limparSelecaoDeEdicao();
          this.opcoesCard = false;
        },
        error: (err) => console.error('Erro ao cancelar corte:', err)
      });
  }

  excluirCorte(id: number): void {
    this.corteService.excluirCorte(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.buscarCortes();
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao excluir corte:', err)
      });
  }

  // =======================================================================
  // UI: card de opções e exibição
  // =======================================================================

  abrirModalOpcao(corte: CorteResponse): void {
    if (this.corteAbertoOpcao === corte) {
      this.opcoesCard = !this.opcoesCard;
    } else {
      this.corteAbertoOpcao = corte;
      this.opcoesCard = true;
    }
  }

  pegarStatusParaEstilo(status: string | undefined): string {
    return status ? status.toLowerCase() : '';
  }

  getNomeMesFormatado(mes: string): string {
    if (!mes) return '';

    const [ano, mesNumero] = mes.split('-');
    const nomeMes = NOMES_MESES[Number(mesNumero) - 1];

    return `${nomeMes} de ${ano}`;
  }

  getquantidadeDeCortes(): number {
    return this.cortesFiltrados.length;
  }

  // =======================================================================
  // Filtros em tempo real (status + nome/lote), aplicados no front
  // =======================================================================

  get filtrosAtivos(): boolean {
    return this.termoBusca.trim() !== '' || this.filtroStatus !== 'TODOS';
  }

  onTermoBuscaChange(valor: string): void {
    this.termoBusca = valor ?? '';
    this.aplicarFiltros();
  }

  selecionarFiltroStatus(status: FiltroStatus): void {
    this.filtroStatus = status;
    this.aplicarFiltros();
  }

  limparFiltros(): void {
    this.termoBusca = '';
    this.filtroStatus = 'TODOS';
    this.aplicarFiltros();
  }

  /**
   * Recalcula a lista exibida e a contagem de cada status a partir de cortesBuscados.
   * Deve ser chamado sempre que a lista ou algum filtro mudar.
   *
   * As contagens dos chips respeitam a busca por texto (mas não o status selecionado),
   * assim cada chip mostra quantos resultados teria se fosse clicado.
   */
  aplicarFiltros(): void {
    const termo = this.normalizar(this.termoBusca);

    const porTexto = termo
      ? this.cortesBuscados.filter(c =>
          this.normalizar(c.nomeModelo).includes(termo) ||
          this.normalizar(c.loteFormatado).includes(termo)
        )
      : this.cortesBuscados;

    const contagens = contagensZeradas();
    contagens.TODOS = porTexto.length;

    for (const corte of porTexto) {
      const status = this.statusDe(corte);
      if (status in contagens && status !== 'TODOS') {
        contagens[status as StatusCorte]++;
      }
    }

    this.contagens = contagens;
    this.cortesFiltrados = this.filtroStatus === 'TODOS'
      ? porTexto
      : porTexto.filter(c => this.statusDe(c) === this.filtroStatus);
  }

  private statusDe(corte: CorteResponse): string {
    return String(corte.corteStatus ?? '').toUpperCase();
  }

  /** Minúsculas e sem acentos, para a busca ignorar maiúsculas e acentuação. */
  private normalizar(valor: string | null | undefined): string {
    return (valor ?? '')
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .trim();
  }

  // =======================================================================
  // Utilitários privados
  // =======================================================================

  /** Mês atual no formato yyyy-MM (o mesmo do <input type="month">). */
  private getMesAtualFormatado(): string {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    return `${ano}-${mes}`;
  }

  /**
   * Converte data de dd/MM/yyyy (formato retornado pelo backend) para yyyy-MM-dd (ISO),
   * que é o formato exigido pelo backend ao ENVIAR uma data e pelo <input type="date">.
   *
   * É idempotente: se a data já estiver em ISO, retorna sem alterar.
   * Isso permite chamar este método em qualquer ponto do fluxo (exibição ou envio)
   * sem risco de converter duas vezes.
   */
  private converterDataParaISO(data: string | null | undefined): string | null {
    if (!data) return null;

    // Já está em ISO (yyyy-MM-dd) -> retorna como está
    if (/^\d{4}-\d{2}-\d{2}$/.test(data)) {
      return data;
    }

    // Está em dd/MM/yyyy -> converte
    if (/^\d{2}\/\d{2}\/\d{4}$/.test(data)) {
      const [dia, mes, ano] = data.split('/');
      return `${ano}-${mes}-${dia}`;
    }

    console.warn('Formato de data inesperado ao converter para ISO:', data);
    return data;
  }

  private montarCorteUpdateRequest(
    corte: CorteResponse,
    idCortador: number | null,
    idEnfestador: number | null
  ): CorteUpdateRequest {

    return {
      dataDeCorte: this.converterDataParaISO(corte.dataDeCorte),
      loteFormatado: corte.loteFormatado,
      nomeModelo: corte.nomeModelo,
      quantidadeTotal: corte.quantidadeTotal,
      observacao: corte.observacao,
      idCortador: idCortador,
      idEnfestador: idEnfestador
    };
  }

  private substituirCorteNaLista(corteAtualizado: CorteResponse): void {
    const index = this.cortesBuscados.findIndex(c => c.id === corteAtualizado.id);
    if (index !== -1) {
      this.cortesBuscados[index] = corteAtualizado;
      this.aplicarFiltros();
    }
  }

  private limparSelecaoDeEdicao(): void {
    this.corteSelecionado = null;
    this.corteEdicao = null;
    this.idCortador = null;
    this.idEnfestador = null;
  }
}