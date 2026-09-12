import { ChangeDetectorRef, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { EnfestadorService } from '../../services/enfestador/enfestador-service';
import { CortadorService } from '../../services/cortador/cortador-service';
import { CorteService } from '../../services/corte/corte-service';
import { LoteService } from '../../services/lote/lote-service';

import { EnfestadorRequest } from '../../interfaces/enfestador/EnfestadorRequest';
import { CortadorRequest } from '../../interfaces/cortador/CortadorRequest';
import { EnfestadorOverview } from '../../interfaces/enfestador/EnfestadorOverview';
import { CortadorOverview } from '../../interfaces/cortador/CortadorOverview';

type TipoItemExclusao = 'cortador' | 'enfestador';

interface ItemParaExcluir {
  id: number | null;
  tipo: TipoItemExclusao;
  nome: string | null;
}

interface PessoaSelecionada {
  id: number;
  tipo: TipoItemExclusao;
  nome: string;
  quantidadeDeCortes: number;
  isAtivo: boolean;
  dataDeCadastro: string;
  avatarEmoji: string;
  avatarBg: string;
  avatarCor: string;
}

const AVATARES_ANIMAIS = [
  { emoji: '🐵', bg: '#eafaf0', cor: '#964B00' },
  { emoji: '🦊', bg: '#fdf1e2', cor: '#d98324' },
  { emoji: '🐼', bg: '#eef2f7', cor: '#475569' },
  { emoji: '🐱', bg: '#fdeef2', cor: '#d63384' },
  { emoji: '🐶', bg: '#eef6ff', cor: '#2563eb' },
  { emoji: '🦁', bg: '#fff7e6', cor: '#d97706' },
  { emoji: '🐨', bg: '#f1f0fb', cor: '#6d5bd0' },
  { emoji: '🐯', bg: '#fff1e6', cor: '#ea580c' },
  { emoji: '🐵', bg: '#faf1e6', cor: '#a35d1f' }
] as const;

const cortadorRequestVazio = (): CortadorRequest => ({ nome: '' });
const enfestadorRequestVazio = (): EnfestadorRequest => ({ nome: '' });

@Component({
  selector: 'app-config-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './config-component.html',
  styleUrl: './config-component.scss'
})
export class ConfigComponent implements OnInit {

  // ---------------------------------------------------------------------
  // Injeção de dependências
  // ---------------------------------------------------------------------
  private readonly destroyRef = inject(DestroyRef);
  private readonly cdr = inject(ChangeDetectorRef);

  private readonly enfestadorService = inject(EnfestadorService);
  private readonly cortadorService = inject(CortadorService);
  private readonly corteService = inject(CorteService);
  private readonly loteService = inject(LoteService);

  // ---------------------------------------------------------------------
  // Estado do lote
  // ---------------------------------------------------------------------
  loteAtual: string | null = null;
  modalIncrementoLote = false;
  modalDecrementoLote = false;

  // ---------------------------------------------------------------------
  // Detalhes da pessoa (modal de cortador/enfestador)
  // ---------------------------------------------------------------------
  pessoaSelecionada: PessoaSelecionada | null = null;

  private escolherAvatarAnimal(id: number): { avatarEmoji: string; avatarBg: string; avatarCor: string } {
    const avatar = AVATARES_ANIMAIS[id % AVATARES_ANIMAIS.length];
    return { avatarEmoji: avatar.emoji, avatarBg: avatar.bg, avatarCor: avatar.cor };
  }

  // ---------------------------------------------------------------------
  // Listagens
  // ---------------------------------------------------------------------
  enfestadores: EnfestadorOverview[] = [];
  cortadores: CortadorOverview[] = [];

  // ---------------------------------------------------------------------
  // Cadastro de cortador / enfestador
  // ---------------------------------------------------------------------
  modoCadastroCortador = false;
  modoCadastroEnfestador = false;

  novoCortador: CortadorRequest = cortadorRequestVazio();
  novoEnfestador: EnfestadorRequest = enfestadorRequestVazio();

  successCortador = false;
  successEnfestador = false;

  // ---------------------------------------------------------------------
  // Exclusão de cortador / enfestador
  // ---------------------------------------------------------------------
  modalExclusao = false;
  itemParaExcluir: ItemParaExcluir | null = null;

  // ---------------------------------------------------------------------
  // Validação
  // ---------------------------------------------------------------------
  erros: Record<string, string> = {};

  // =======================================================================
  // Ciclo de vida
  // =======================================================================

  ngOnInit(): void {
    this.buscarLoteAtual();
    this.buscarEnfestadores();
    this.buscarCortadores();
  }

  // =======================================================================
  // Busca de dados
  // =======================================================================

  buscarLoteAtual(): void {
    this.loteService.buscarLote()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => { this.loteAtual = data.numero_lote; this.cdr.detectChanges(); },
        error: (err) => console.error('Erro ao buscar lote atual:', err)
      });
  }

  buscarEnfestadores(): void {
    this.enfestadorService.buscarDetalhesEnfestadores()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => { this.enfestadores = data; this.cdr.detectChanges(); },
        error: (err) => console.error('Erro ao buscar enfestadores:', err)
      });
  }

  buscarCortadores(): void {
    this.cortadorService.buscarDetalhesCortadores()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => { this.cortadores = data; this.cdr.detectChanges(); },
        error: (err) => console.error('Erro ao buscar cortadores:', err)
      });
  }

  // =======================================================================
  // Cadastro de cortador / enfestador
  // =======================================================================

  abrirModoCriacaoCortador(): void {
    this.erros = {};
    this.successCortador = false;
    this.modoCadastroCortador = !this.modoCadastroCortador;
  }

  abrirModoCriacaoEnfestador(): void {
    this.erros = {};
    this.successEnfestador = false;
    this.modoCadastroEnfestador = !this.modoCadastroEnfestador;
  }

  cadastrarCortador(): void {
    this.erros = {};

    if (!this.novoCortador.nome.trim()) {
      this.erros['novoCortador'] = 'Nome obrigatório';
    }

    if (Object.keys(this.erros).length > 0) return;

    this.cortadorService.cadastrarCortador(this.novoCortador)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.successCortador = true;
          this.successEnfestador = false;
          this.novoCortador = cortadorRequestVazio();
          this.buscarCortadores();
          this.modoCadastroCortador = false;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao cadastrar cortador:', err)
      });
  }

  cadastrarEnfestador(): void {
    this.erros = {};

    if (!this.novoEnfestador.nome.trim()) {
      this.erros['novoEnfestador'] = 'Nome obrigatório';
    }

    if (Object.keys(this.erros).length > 0) return;

    this.enfestadorService.cadastrarEnfestador(this.novoEnfestador)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.successEnfestador = true;
          this.successCortador = false;
          this.novoEnfestador = enfestadorRequestVazio();
          this.buscarEnfestadores();
          this.modoCadastroEnfestador = false;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao cadastrar enfestador:', err)
      });
  }

  // =======================================================================
  // Exclusão de cortador / enfestador
  // =======================================================================

  abrirModalExclusao(id: number | null, tipo: TipoItemExclusao, nome: string | null): void {
    this.itemParaExcluir = { id, tipo, nome };
    this.modalExclusao = true;
  }

  fecharModalExclusao(): void {
    this.modalExclusao = false;
  }

  confirmarExclusao(): void {
    if (!this.itemParaExcluir) return;

    if (this.itemParaExcluir.tipo === 'cortador') {
      this.excluirCortador(this.itemParaExcluir.id);
    } else {
      this.excluirEnfestador(this.itemParaExcluir.id);
    }
  }

  abrirDetalhesCortador(cortador: CortadorOverview): void {
    this.pessoaSelecionada = {
      id: cortador.idCortador,
      tipo: 'cortador',
      nome: cortador.nome,
      quantidadeDeCortes: cortador.quantidadeDeCortes,
      isAtivo: cortador.isAtivo,
      dataDeCadastro: cortador.dataDeCadastro,
      ...this.escolherAvatarAnimal(cortador.idCortador)
    };
  }

  abrirDetalhesEnfestador(enfestador: EnfestadorOverview): void {
    this.pessoaSelecionada = {
      id: enfestador.idEnfestador,
      tipo: 'enfestador',
      nome: enfestador.nome,
      quantidadeDeCortes: enfestador.quantidadeDeCortes,
      isAtivo: enfestador.isAtivo,
      dataDeCadastro: enfestador.dataDeCadastro,
      ...this.escolherAvatarAnimal(enfestador.idEnfestador)
    };
  }

  fecharDetalhesPessoa(): void {
    this.pessoaSelecionada = null;
  }

  excluirPessoaSelecionada(): void {
    if (!this.pessoaSelecionada) return;

    const { id, tipo, nome } = this.pessoaSelecionada;
    this.fecharDetalhesPessoa();
    this.abrirModalExclusao(id, tipo, nome);
  }

  private excluirCortador(id: number | null): void {
    this.cortadorService.deletarCortador(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.buscarCortadores();
          this.modalExclusao = false;
          this.successCortador = false;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao excluir cortador:', err)
      });
  }

  private excluirEnfestador(id: number | null): void {
    this.enfestadorService.deletarEnfestador(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.buscarEnfestadores();
          this.modalExclusao = false;
          this.successEnfestador = false;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao excluir enfestador:', err)
      });
  }

  // =======================================================================
  // Lote (incremento / decremento)
  // =======================================================================

  abrirModalIncrementoLote(): void {
    this.modalIncrementoLote = true;
  }

  fecharModalIncrementoLote(): void {
    this.modalIncrementoLote = false;
  }

  abrirModalDecrementoLote(): void {
    this.modalDecrementoLote = true;
  }

  fecharModalDecrementoLote(): void {
    this.modalDecrementoLote = false;
  }

  incrementarLote(): void {
    if (this.loteAtual == null) return;

    this.loteService.incrementarLote()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.buscarLoteAtual();
          this.modalIncrementoLote = false;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao incrementar lote:', err)
      });
  }

  decrementarLote(): void {
    if (this.loteAtual == null) return;

    this.loteService.decrementarLote()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.buscarLoteAtual();
          this.modalDecrementoLote = false;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao decrementar lote:', err)
      });
  }
}