import { Component, computed, inject, OnDestroy, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RelatorioService } from '../../services/relatorio/relatorio-service';

@Component({
  selector: 'app-relatorio-component',
  imports: [FormsModule],
  templateUrl: './relatorio-component.html',
  styleUrl: './relatorio-component.scss',
})
export class RelatorioComponent implements OnDestroy {

  private relatorioService = inject(RelatorioService);
  private sanitizer = inject(DomSanitizer);

  dataInicial = signal(this.primeiroDiaDoMes());
  dataFinal = signal(this.hoje());

  carregando = signal(false);
  erro = signal('');

  previewUrl = signal<SafeResourceUrl | null>(null);
  private previewObjectUrl: string | null = null;
  private ultimoBlob: Blob | null = null;

  periodoInvalido = computed(() =>
    !this.dataInicial() || !this.dataFinal() || this.dataInicial() > this.dataFinal()
  );

  visualizar() {
    this.buscar(blob => {
      this.limparPreview();
      this.ultimoBlob = blob;
      this.previewObjectUrl = URL.createObjectURL(blob);
      this.previewUrl.set(this.sanitizer.bypassSecurityTrustResourceUrl(this.previewObjectUrl));
    });
  }

  baixar() {
    // Se já existe um preview do mesmo período, reaproveita o Blob
    if (this.ultimoBlob && this.previewUrl()) {
      this.relatorioService.baixarArquivo(this.ultimoBlob, this.nomeArquivo());
      return;
    }
    this.buscar(blob => this.relatorioService.baixarArquivo(blob, this.nomeArquivo()));
  }

  fecharPreview() {
    this.limparPreview();
  }

  private buscar(aoReceber: (blob: Blob) => void) {
    if (this.periodoInvalido()) {
      this.erro.set('Informe um período válido.');
      return;
    }

    this.carregando.set(true);
    this.erro.set('');

    this.relatorioService.gerarRelatorioCortesPdf(this.dataInicial(), this.dataFinal()).subscribe({
      next: blob => {
        aoReceber(blob);
        this.carregando.set(false);
      },
      error: e => {
        this.erro.set(e.message ?? 'Erro ao gerar o relatório.');
        this.carregando.set(false);
      },
    });
  }

  private nomeArquivo(): string {
    return `relatorio-cortes_${this.dataInicial()}_a_${this.dataFinal()}.pdf`;
  }

  private limparPreview() {
    if (this.previewObjectUrl) URL.revokeObjectURL(this.previewObjectUrl);
    this.previewObjectUrl = null;
    this.ultimoBlob = null;
    this.previewUrl.set(null);
  }

  private hoje(): string {
    return this.formatar(new Date());
  }

  private primeiroDiaDoMes(): string {
    const d = new Date();
    return this.formatar(new Date(d.getFullYear(), d.getMonth(), 1));
  }

  private formatar(d: Date): string {
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const dia = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${mes}-${dia}`;
  }

  ngOnDestroy() {
    this.limparPreview();
  }
}