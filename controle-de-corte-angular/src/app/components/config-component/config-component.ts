import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { EnfestadorService } from '../../services/enfestador/enfestador-service';
import { CortadorService } from '../../services/cortador/cortador-service';
import { EnfestadorRequest } from '../../interfaces/enfestador/EnfestadorRequest';
import { CortadorRequest } from '../../interfaces/cortador/CortadorRequest';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CorteService } from '../../services/corte/corte-service';
import { LoteService } from '../../services/lote/lote-service';
import { EnfestadorOverview } from '../../interfaces/enfestador/EnfestadorOverview';
import { CortadorOverview } from '../../interfaces/cortador/CortadorOverview';

@Component({
  selector: 'app-config-component',
  imports : [CommonModule, FormsModule],
  templateUrl: './config-component.html',
  styleUrls: ['./config-component.scss'],
})
export class ConfigComponent implements OnInit{

  enfestadorService = inject(EnfestadorService);
  cortadorService = inject(CortadorService);
  cdr = inject(ChangeDetectorRef);
  loteService = inject(LoteService);
  corteService = inject(CorteService);

  loteAtual : string | null = null;

  enfestadores : EnfestadorOverview[] = [];
  cortadores : CortadorOverview[] = [];

  erros: { [key: string]: string } = {};

  modoCadastroCortador = false;
  modoCadastroEnfestador = false;

  nomeParaBuscar !: string;

  novoCortador : CortadorRequest = {
    nome:''
  };
  novoEnfestador : EnfestadorRequest = {
    nome : ''
  };
  successCortador = false;
  successEnfestador = false;

  modalExclusao = false;

  itemExcluido !: {
    id: number | null;
    tipo: 'cortador' | 'enfestador';
    nome: string | null;
  }

  modalDecrementarLote = false;
  modalIncrementoLote = false;

  ngOnInit() {
    this.buscarLote();
    this.buscarEnfestadores();
    this.buscarCortadores();
  }

  buscarEnfestadores(){
    this.enfestadorService.buscarDetalhesEnfestadores().subscribe({
      next: (data) => {this.enfestadores = data; this.cdr.detectChanges()},
      error: (err) => {console.log(err)}
    });
  }

  buscarCortadores(){
    this.cortadorService.buscarDetalhesCortadores().subscribe({
      next: (data) => {this.cortadores = data; this.cdr.detectChanges()},
      error: (err) => {console.log(err)}
    })
  }

  cadastrarCortador(){

    this.erros = {}

    if(!this.novoCortador.nome.trim()){
      this.erros['novoCortador'] = 'Nome obrigatório'
    }

    if (Object.keys(this.erros).length > 0) {
      return;
    }

    this.cortadorService.cadastrarCortador(this.novoCortador).subscribe({
      next : (data) => {
        this.successCortador = true;
        this.successEnfestador = false;
        console.log("Cortador cadastrado com sucesso!");
        this.buscarCortadores()
        this.modoCadastroCortador = false;
        this.cdr.detectChanges();
      },
      error : (err) => {console.log(err)}
    })
  }

  cadastrarEnfestador(){

    this.erros = {}

    if(!this.novoEnfestador.nome.trim()){
      this.erros['novoEnfestador'] = 'Nome obrigatório'
    }

    if (Object.keys(this.erros).length > 0) {
      return;
    }

    this.enfestadorService.cadastrarEnfestador(this.novoEnfestador).subscribe({
      next : (data) => {
        this.successEnfestador = true;
        this.successCortador = false;
        console.log("Enfestador cadastrado com sucesso!");      
        this.buscarEnfestadores()
        this.modoCadastroEnfestador = false;
        this.cdr.detectChanges();
      },
      error : (err) => {console.log(err)}
    })
  }

  abrirModoCriacaoCortador(){
   this.modoCadastroCortador = (this.modoCadastroCortador === true) ? false : true;
  }

   abrirModoCriacaoEnfestador(){
    this.modoCadastroEnfestador = (this.modoCadastroEnfestador === true) ? false : true;
  }

  ativarModalExclusao(id:number | null, tipo : 'cortador' | 'enfestador', nome:string | null){

    this.itemExcluido = {
      id,
      tipo,
      nome
    };

    this.modalExclusao = true;
  }

  desativarModalExclusao(){
    this.modalExclusao = false;
  }

  deletarCortadorOuEnfestador(){

    if(this.itemExcluido.tipo === 'cortador'){
      this.cortadorService.deletarCortador(this.itemExcluido.id).subscribe({
        next: (data) =>  {
          this.buscarCortadores();
          this.modalExclusao=false; 
          this.successCortador = false
          this.cdr.detectChanges();},
        error: (err) => {console.log(err)}
      })
    }
    else{
      this.enfestadorService.deletarEnfestador(this.itemExcluido.id).subscribe({
        next: (data) => {
          this.buscarEnfestadores();
          this.modalExclusao=false; 
          this.successEnfestador = false
          this.cdr.detectChanges();
        },
        error: (err) => {console.log(err)}
      })
    }
  }

  desativarModalDecrementoLote(){
    this.modalDecrementarLote = false;
  }

  ativarModalDecrementoLote(){

    this.modalDecrementarLote = true;
  }

  ativarModalIncrementoLote(){
    this.modalIncrementoLote = true;
  }

  desativarModalIncrementoLote(){
    this.modalIncrementoLote = false;
  }

  buscarLote(){
    this.loteService.buscarLote().subscribe({
        next: (data) => {this.loteAtual = data.numero_lote; this.cdr.detectChanges()},
        error: (err) => {console.log(err)}
      })
  }

  decrementarLote(){

    if(this.loteAtual == null){
      return
    }

    this.loteService.decrementarLote().subscribe({
      next: () => {console.log("Lote decrementado com sucesso!"); this.buscarLote(); this.modalDecrementarLote=false; this.cdr.detectChanges()} ,
      error: (err) => {console.log(err)}
    })
  }

  incrementarLote(){
    if(this.loteAtual == null){
      return
    }

    this.loteService.incrementarLote().subscribe({
      next: () => {console.log("Lote incrementado com sucesso!"); this.buscarLote(); this.modalIncrementoLote = false ;this.cdr.detectChanges()},
    })
  }
}
