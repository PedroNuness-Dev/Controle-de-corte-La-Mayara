import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SuporteService } from '../../services/suporte-service';
import { SuporteDtoRequest } from '../../interfaces/SuporteDtoRequest';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-suporte-component',
  imports: [FormsModule],
  templateUrl: './suporte-component.html',
  styleUrl: './suporte-component.scss',
})
export class SuporteComponent {

  suporteService = inject(SuporteService);
  cdr = inject(ChangeDetectorRef);

  opcaoSelecionada : string = '';

  textoParaMandar : string = '';
  telaCarregadandoModal = false;

  messageErro = false;
  modalSucesso =false
  modalfailure = false;

  assuntos: string[] = [
    'Erro no sistema',
    'Sistema lento',
    'Cadastro ou atualização de dados',
    "Erro ao cadastrar, atualizar ou deletar corte",
    'Sugestão de melhoria',
    'Dúvida sobre utilização',
    'Outro'
  ];

  enviarSuporte(){

    this.messageErro = false;
    this.modalSucesso = false;
    this.modalfailure = false;

    if(this.opcaoSelecionada.trim() === '' || this.textoParaMandar.trim() === ''){
      this.messageErro = true;
      return;
    }

    this.telaCarregadandoModal = true;

    const suporteDtoRequest : SuporteDtoRequest ={
      titulo : this.opcaoSelecionada,
      texto : this.textoParaMandar
    }

    this.suporteService.mandarEmail(suporteDtoRequest).pipe(
          finalize(() => {
            setTimeout(() => {
              this.telaCarregadandoModal = false;
              this.cdr.detectChanges();}, 300);            
          })
         )
         .subscribe({
          next: (data) => {
            console.log('Sucesso!', data);
            this.modalSucesso = true;  
            this.opcaoSelecionada = '';
            this.textoParaMandar = '';
            this.cdr.detectChanges();},
          error : (err) => {console.log("Erro ao enviar email!"); console.log(err); this.modalfailure = true; this.cdr.detectChanges()}
        })
  }
}
