import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CorteService } from '../../services/corte/corte-service';
import { CorteResponse } from '../../interfaces/corte/CorteResponse';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CorteUpdateRequest } from '../../interfaces/corte/CorteUpdate';
import { CorteRequest } from '../../interfaces/corte/CorteRequest';
import { LoteService } from '../../services/lote/lote-service';
import { finalize } from 'rxjs';
import { CortadorResponse } from '../../interfaces/cortador/CortadorResponse';
import { EnfestadorResponse } from '../../interfaces/enfestador/EnfestadorResponse';
import { EnfestadorService } from '../../services/enfestador/enfestador-service';
import { CortadorService } from '../../services/cortador/cortador-service';
import { RelatorioService } from '../../services/relatorio/relatorio-service';


@Component({
  selector: 'app-content-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './content-component.html',
  styleUrl: './content-component.scss'
})
export class ContentComponent implements OnInit{

  route = inject(ActivatedRoute);
  cdr = inject(ChangeDetectorRef)
  corteService = inject(CorteService);
  loteService = inject(LoteService);
  enfestadorService = inject(EnfestadorService);
  cortadorService = inject(CortadorService);
  relatorioService = inject(RelatorioService);

  cortesDoMes : CorteResponse[] = [];
  pageSelected  = 'Geral'
  anoAtual = new Date().getFullYear();
  relatorioDeCortes : number = 0;
  loteAtual !: string;
  cortesRegistradosNoMes !: number;
  corteAdicionadoRecentemente !: CorteResponse | null;
  nomeParaBuscar !: string;
  telaCarregadandoModal = false;

  cortadores : CortadorResponse[] = [];
  enfestadores : EnfestadorResponse[] =[];

  meses = [
    "JANEIRO","FEVEREIRO","MARÇO","ABRIL","MAIO","JUNHO","JULHO","AGOSTO","SETEMBRO","OUTUBRO","NOVEMBRO","DEZEMBRO"
  ]

  mesAtual = this.meses[new Date().getMonth()];

  corteSelecionado : CorteResponse | null = null;
  corteEdicao : CorteResponse | null = null ;
  idEnfestador : number | null = null;
  idCortador : number | null = null;
  modoCriacao = false;
  novoCorte : CorteRequest = {
      nomeModelo: '',
      quantidadeTotal: null,
      dataDeRegistro:  new Date().toISOString().split('T')[0],
      observacao: ''
  };
  tipoLote = 'atual'
  opcoesCard = false;
  corteAbertoOpcao !: CorteResponse;

  erros: { [key: string]: string } = {};

  ngOnInit(){
    this.route.params.subscribe(params => {
      this.pageSelected = params['tipo'] || 'Geral';
      this.buscarCortesDoMesPorStatus();
      this.nomeParaBuscar = '';
    })


    this.buscarRelatoiro();
    this.buscarLoteAtual();
    this.buscarCortadores();
    this.buscarEnfestadores();
  }

  buscarRelatoiro(){

    const anoAtual = new Date().getFullYear();
    const mesAtual = new Date().getMonth() + 1;

    this.relatorioService.buscarRelatorioDoMes(mesAtual,anoAtual).subscribe({
      next: (data) => {this.relatorioDeCortes = data.quantidade; console.log(`Quantidade de cortes buscadas: ${data.quantidade}`)},
      error:(err) => {console.log(err)}
    })

  }

  buscarCortadores(){

    this.cortadorService.buscarCortadores().subscribe({
      next: (data) => {this.cortadores = data; this.cdr.detectChanges()},
      error : (err) => {console.log(err)}
    })
  }

  buscarEnfestadores(){

    this.enfestadorService.buscarEnfestadores().subscribe({
      next: (data) => {this.enfestadores = data; this.cdr.detectChanges(); console.log("Enfestadores buscados com sucesso!")},
      error : (err) => {console.log(err)}
    })
  }

  abrirCriacao(){
    this.modoCriacao = true;
  }

  fecharCriacao(){
    this.modoCriacao = false;
  }

  fecharModalCorte(){
    this.corteSelecionado = null;
  }

  buscarCortesDoMesPorStatus(){

    const anoAtual = new Date().getFullYear();
    const mesAtual = new Date().getMonth() + 1;

    if(this.pageSelected === 'Geral'){
        this.corteService.buscarCortesDoMes(mesAtual,anoAtual).subscribe({
          next: (data) => {
            this.cortesDoMes = data;
            this.cdr.detectChanges();},
          error: (err) => {console.log(err)}
        })
      }
      else{        
        let statusParaMandar !: string;

        if(this.pageSelected === 'Pendentes'){
          statusParaMandar = 'pendente';
        }
        else if(this.pageSelected === 'Enfestados'){
          statusParaMandar = 'enfestado';
        }
        else if(this.pageSelected === 'Cortados'){
          statusParaMandar = 'cortado'
        }

        this.corteService.buscarCortesDoMesPorStatus(statusParaMandar,anoAtual, mesAtual).subscribe({
          next: (data) => {
            this.cortesDoMes = data;
            this.cdr.detectChanges();},
          error: (err) => {console.log(err)}
        })
      }      
  }

  buscarLoteAtual(){
    this.loteService.buscarLote().subscribe({
      next: (data) => {this.loteAtual = data.numero_lote; this.cdr.detectChanges()},
      error: (err) => {console.log(err)}
    })
  }

  selecionarCorte(corte:CorteResponse){

    this.erros = {}

      this.corteSelecionado = corte;

      if(corte.dataDeCorte == null){
        this.corteEdicao = {
          ...corte
        };
      }
      else{
        this.corteEdicao = {
        ...corte,
        dataDeCorte: this.formatarData(corte.dataDeCorte)
      };
      }

      this.idEnfestador = corte.enfestador?.id ?? null;
      this.idCortador = corte.cortador?.id ?? null;
      this.opcoesCard = false;
  }

  pegarStatusParaEstilo(status:string):string{

    return status.toLowerCase();
  }

  atualizarCorte(){

   const corteParaAtualizar: CorteUpdateRequest = {
      dataDeCorte: this.corteEdicao!.dataDeCorte,
      loteFormatado: this.corteEdicao!.loteFormatado,
      nomeModelo: this.corteEdicao!.nomeModelo,
      quantidadeTotal: this.corteEdicao!.quantidadeTotal,
      observacao: this.corteEdicao!.observacao,
      idCortador: this.idCortador ?? null,
      idEnfestador: this.idEnfestador ?? null
    }

    if(!this.validarAtualizacao(corteParaAtualizar)){
      return;
    }

    this.corteService.atualizarCorte(this.corteSelecionado!.id, corteParaAtualizar).subscribe({
      next: (data) => {
        console.log("Corte atualizado com sucesso");
        if(this.corteSelecionado!.id === this.corteAdicionadoRecentemente?.id){
          this.corteAdicionadoRecentemente = data;
        }

        const index = this.cortesDoMes.findIndex(
          corte => corte.id === data.id
        );

        if(index !== -1){
          this.cortesDoMes[index] = data;
        }
        
        this.corteSelecionado = null;
        this.corteEdicao = null;
        this.idCortador = null;
        this.idEnfestador = null;
        this.cdr.detectChanges();
      },
      error: (err) => {console.log(err)}
    })
  }

  validarAtualizacao(corte : CorteUpdateRequest) : boolean{

    this.erros = {}

    if(!corte.loteFormatado.trim()){
      this.erros['loteAtt'] = 'Lote obrigatório';
    }
    else if (!/^[0-9/]+$/.test(corte.loteFormatado)) {
      this.erros['loteAtt'] = 'O lote deve conter apenas números e "/"';
    }
    else if (!corte.loteFormatado.includes('/')) {
      this.erros['loteAtt'] = 'O lote deve conter "/"';
    }
    if (!corte.nomeModelo.trim()){
      this.erros['nomeModeloAtt'] = 'Nome do modelo obrigatório';
    }
    if(!corte.quantidadeTotal || corte.quantidadeTotal <= 0){
      this.erros['quantidadeAtt'] = 'Quantidade obrigatória';
    }
    if(corte.idCortador != null && corte.idEnfestador == null){
      this.erros["enfestadorAttError"] = "Selecione um enfestador"
    }        
    if(corte.dataDeCorte == null && corte.idCortador != null){
      this.erros["dataCorteAtt"] = "Selecione a data de corte"
    }
    
    return Object.keys(this.erros).length === 0
  }

  formatarData(data:string):string{

    let dataVetor = data.split("/");
    return dataVetor[2]+"-"+dataVetor[1]+"-"+dataVetor[0]
  }

  adicionarCorte(){

    this.erros = {};

    if(this.novoCorte.dataDeRegistro === ''){
      this.erros['dataRegistro'] = 'Data de Registro obrigatória'
    }

    if(!this.novoCorte.nomeModelo.trim()){
      this.erros['nomeModelo'] = 'Nome do modelo obrigatório';
    }

    if(!this.novoCorte.quantidadeTotal || this.novoCorte.quantidadeTotal === 0){
      this.erros['quantidade'] = 'Quantidade obrigatória'
    }

    if (Object.keys(this.erros).length > 0) {
      return;
    }

    if ( this.tipoLote === 'novo'){
      this.loteService.incrementarLote().subscribe({
        next: (data) => {console.log("Lote incrementado com sucesso!"); this.cdr.detectChanges(); this.salvarCorte()},
        error: (err) => {console.log(err);}
      });
    }
    else{
      this.salvarCorte();
    }
  }

  salvarCorte(){

    const novoCorteParaEnviar: CorteRequest = this.novoCorte;

    this.corteService.adicionarCorte(novoCorteParaEnviar).subscribe({
      next: (data) => {
        this.corteAdicionadoRecentemente = data;
        this.novoCorte = {
          nomeModelo: '',
          quantidadeTotal: null,
          dataDeRegistro:  new Date().toISOString().split('T')[0],
          observacao: ''
        }
        this.fecharCriacao();
        this.buscarLoteAtual();
        this.cdr.detectChanges();
        this.buscarRelatoiro();
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  cancelarCorte(corte : CorteResponse){

    this.corteService.cancelarCorte(corte.id).subscribe({
      next: (data) => {

        if (this.corteAdicionadoRecentemente?.id === corte.id){
            this.corteAdicionadoRecentemente.corteStatus = 'CANCELADO';
        }

        this.buscarCortesDoMesPorStatus();
        this.corteSelecionado = null;
        this.corteEdicao = null;
        this.idCortador = null;
        this.idEnfestador = null;
        this.opcoesCard = false},
      error: (err) => {console.log(err)}
    })
  }

  buscarCortePorNomeOuLote(){

    if(this.nomeParaBuscar === '') this.buscarCortesDoMesPorStatus();
  
    const anoAtual = new Date().getFullYear();
    const mesAtual = new Date().getMonth() + 1;
    this.telaCarregadandoModal = true;

    this.corteService.buscarCortePorNomeOuLote(this.nomeParaBuscar, mesAtual, anoAtual).pipe(
          finalize(() => {
            setTimeout(() => {
              this.telaCarregadandoModal = false;
              this.cdr.detectChanges();}, 300);            
          })
         ).subscribe({
            next: (data) => {
              this.cortesDoMes = data;
              this.cdr.detectChanges();
          },
      error: (err) => {console.log(err)}
    })
  }

  atualizarPagina(){

    this.telaCarregadandoModal = true;
    setTimeout(() => {
              this.telaCarregadandoModal = false;
              this.cdr.detectChanges();
              window.location.reload();}, 300);
  }

  abrirModalOpcao(corte : CorteResponse){

    if(this.corteAbertoOpcao === corte){
      this.opcoesCard = !this.opcoesCard;
    }
    else{
      this.corteAbertoOpcao = corte;
      this.opcoesCard = true;
    }
  }

  excluirCorte(id:number){

    if (id === this.corteAdicionadoRecentemente?.id){
      this.corteAdicionadoRecentemente = null;
    }

    this.corteService.excluirCorte(id).subscribe({
      next: () => {this.cdr.detectChanges(); console.log("Corte excluido com sucesso!"); this.buscarCortesDoMesPorStatus(); this.buscarRelatoiro()},
      error: (err) => {console.log(err)}
    })
  }

  ativarCorte(corte:CorteResponse){

    
    const dataFormatada = (corte.dataDeCorte == null) ? null : this.formatarData(corte.dataDeCorte);

    const corteParaAtualizar : CorteUpdateRequest ={
      dataDeCorte: dataFormatada,
      loteFormatado: corte.loteFormatado,
      nomeModelo: corte.nomeModelo,
      quantidadeTotal: corte.quantidadeTotal,
      observacao: corte.observacao,
      idCortador: corte.cortador?.id ?? null,
      idEnfestador: corte.enfestador?.id ?? null
    }

    this.corteService.atualizarCorte(corte.id, corteParaAtualizar).subscribe({
      next: (data) => {
        console.log("Corte atualizado com sucesso");
        if(corte.id === this.corteAdicionadoRecentemente?.id){
          this.corteAdicionadoRecentemente = data;
        }

        const index = this.cortesDoMes.findIndex(
          corteEsc => corteEsc.id === data.id
        );

        if(index !== -1){
          this.cortesDoMes[index] = data;
        }
        
        this.corteSelecionado = null;
        this.corteEdicao = null;
        this.idCortador = null;
        this.idEnfestador = null;
        this.cdr.detectChanges();
      },
      error: (err) => {console.log(err)}
    })
  
    this.opcoesCard = false;
  }
}
