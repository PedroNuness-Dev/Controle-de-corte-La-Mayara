import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SuporteService } from '../../services/suporte/suporte-service';
import { SuporteDtoRequest } from '../../interfaces/suporte/SuporteDtoRequest';
import { finalize } from 'rxjs';

type NivelPrioridade = 'BAIXA' | 'MEDIA' | 'ALTA' | 'URGENTE';

interface OpcaoPrioridade {
  valor: NivelPrioridade;
  label: string;
  icone: string;
}

const PRIORIDADES: OpcaoPrioridade[] = [
  { valor: 'BAIXA', label: 'Baixa', icone: 'south' },
  { valor: 'MEDIA', label: 'Média', icone: 'drag_handle' },
  { valor: 'ALTA', label: 'Alta', icone: 'north' },
  { valor: 'URGENTE', label: 'Urgente', icone: 'priority_high' }
];

@Component({
  selector: 'app-suporte-component',
  imports: [FormsModule],
  templateUrl: './suporte-component.html',
  styleUrl: './suporte-component.scss',
})
export class SuporteComponent {

  private readonly suporteService = inject(SuporteService);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly prioridades = PRIORIDADES;

  opcaoSelecionada: string = '';
  prioridadeSelecionada: NivelPrioridade | null = null;
  textoParaMandar: string = '';

  telaCarregadandoModal = false;

  messageErro = false;
  modalSucesso = false;
  modalfailure = false;

  assuntos: string[] = [
    'Erro no sistema',
    'Sistema lento',
    'Cadastro ou atualização de dados',
    'Erro ao cadastrar, atualizar ou deletar corte',
    'Sugestão de melhoria',
    'Dúvida sobre utilização',
    'Outro'
  ];

  selecionarPrioridade(valor: NivelPrioridade): void {
    this.prioridadeSelecionada = valor;
  }

  enviarSuporte(): void {
    this.messageErro = false;
    this.modalSucesso = false;
    this.modalfailure = false;

    if (
      this.opcaoSelecionada.trim() === '' ||
      this.textoParaMandar.trim() === '' ||
      this.prioridadeSelecionada === null
    ) {
      this.messageErro = true;
      return;
    }

    this.telaCarregadandoModal = true;

    const suporteDtoRequest: SuporteDtoRequest = {
      titulo: this.opcaoSelecionada,
      texto: this.textoParaMandar,
      prioridade: this.prioridadeSelecionada
    };

    this.suporteService.mandarEmail(suporteDtoRequest)
      .pipe(
        finalize(() => {
          setTimeout(() => {
            this.telaCarregadandoModal = false;
            this.cdr.detectChanges();
          }, 300);
        })
      )
      .subscribe({
        next: (data) => {
          console.log('Sucesso!', data);
          this.modalSucesso = true;
          this.opcaoSelecionada = '';
          this.textoParaMandar = '';
          this.prioridadeSelecionada = null;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.log('Erro ao enviar email!');
          console.log(err);
          this.modalfailure = true;
          this.cdr.detectChanges();
        }
      });
  }
}