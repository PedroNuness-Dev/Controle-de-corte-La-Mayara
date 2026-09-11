import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { CorteResponse } from '../../interfaces/corte/CorteResponse';
import { CortadorResponse } from '../../interfaces/cortador/CortadorResponse';
import { EnfestadorResponse } from '../../interfaces/enfestador/EnfestadorResponse';
import { CorteService } from '../../services/corte/corte-service';
import { EnfestadorService } from '../../services/enfestador/enfestador-service';
import { CortadorService } from '../../services/cortador/cortador-service';
import { CorteUpdateRequest } from '../../interfaces/corte/CorteUpdate';

@Component({
  selector: 'app-historico-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './historico-component.html',
  styleUrl: './historico-component.scss',
})
export class HistoricoComponent implements OnInit {

  corteService = inject(CorteService);
  enfestadorService = inject(EnfestadorService);
  cortadorService = inject(CortadorService);
  cdr = inject(ChangeDetectorRef);

  cortesBuscados: CorteResponse[] = [];
  cortadores: CortadorResponse[] = [];
  enfestadores: EnfestadorResponse[] = [];

  mesSelecionado = '';
  mesBuscado = '';
  telaCarregandoModal = false;
  corteSelecionado: CorteResponse | null = null;
  corteEdicao: CorteResponse | null = null;
  idEnfestador: number | null = null;
  idCortador: number | null = null;

  opcoesCard = false;
  corteAbertoOpcao !: CorteResponse;
  infoResult = false;

  erros: { [key: string]: string } = {};

  ngOnInit(): void {
    this.carregarListasDePessoas();
    this.mesSelecionado = this.getMesAtualFormatado();
  }

  carregarListasDePessoas(): void {
    this.enfestadorService.buscarEnfestadores().subscribe({
      next: (data) => { this.enfestadores = data; this.cdr.detectChanges(); },
      error: (err) => { console.log(err); }
    });

    this.cortadorService.buscarCortadores().subscribe({
      next: (data) => { this.cortadores = data; this.cdr.detectChanges(); },
      error: (err) => { console.log(err); }
    });
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
    if(corte.dataDeCorte === '' && corte.idCortador != null){
      this.erros["dataCorteAtt"] = "Selecione a data de corte"
    }     

    return Object.keys(this.erros).length === 0;
  }

  testePesquisar(){
    console.log(this.mesSelecionado);
  }

  buscarCortes(){

    let [ano, mes] = this.mesSelecionado.split("-");
    const anoFormatado = (ano === '') ? null : Number(ano);
    const mesFormatado = (mes === '') ? null : Number(mes);

    this.telaCarregandoModal = true;

    this.corteService.buscarCortesDoMes(mesFormatado, anoFormatado).pipe(
      finalize(() => {
        setTimeout(() => {
          this.telaCarregandoModal = false;
          this.cdr.detectChanges();
        }, 400);
      })
    ).subscribe({
      next: (data) => {
        this.cortesBuscados = data;
        this.mesBuscado = this.mesSelecionado;
        this.infoResult = true;
        this.cdr.detectChanges();
        console.log("Busca feita com sucesso!")
      },
      error: (err) => { console.log(err); }
    });
  }

  selecionarCorte(corte: CorteResponse){
    if (this.corteSelecionado == null || this.corteSelecionado.id !== corte.id){
      this.corteSelecionado = corte;
      this.corteEdicao = {
        ...corte,
        dataDeCorte: this.formatarData(corte.dataDeCorte)
      };
      this.idEnfestador = corte.enfestador?.id ?? null;
      this.idCortador = corte.cortador?.id ?? null;

      this.opcoesCard = false;
    }
    else {
      this.cancelarEdicao();
    }
  }

  atualizarCorte(){
    if (!this.corteSelecionado || !this.corteEdicao) {
      return;
    }

    const corteParaAtualizar = {
      dataDeCorte: this.corteEdicao.dataDeCorte,
      loteFormatado: this.corteEdicao.loteFormatado,
      nomeModelo: this.corteEdicao.nomeModelo,
      quantidadeTotal: this.corteEdicao.quantidadeTotal,
      observacao: this.corteEdicao.observacao,
      idCortador: this.idCortador ?? null,
      idEnfestador: this.idEnfestador ?? null
    };    

    console.log(corteParaAtualizar.dataDeCorte)

    if(!this.validarAtualizacao(corteParaAtualizar)){
      return;
    }

    this.corteService.atualizarCorte(this.corteSelecionado.id, corteParaAtualizar).subscribe({
      next: () => {
        this.cdr.detectChanges();
        this.buscarCortes();
        this.cancelarEdicao();
      },
      error: (err) => { console.log(err); }
    });
  }

  cancelarEdicao(){
    this.corteSelecionado = null;
    this.corteEdicao = null;
    this.idCortador = null;
    this.idEnfestador = null;
  }

  formatarData(data: string): string {
    if (!data) {
      return '';
    }

    if (data.includes('/')){
      const [dia, mes, ano] = data.split('/');
      return `${ano}-${mes}-${dia}`;
    }

    return data;
  }

  pegarStatusParaEstilo(status: string | undefined): string {
    return status ? status.toLowerCase() : '';
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
        
          const index = this.cortesBuscados.findIndex(
            corteEsc => corteEsc.id === data.id
          );
  
          if(index !== -1){
            this.cortesBuscados[index] = data;
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

    this.corteService.excluirCorte(id).subscribe({
      next: () => {this.cdr.detectChanges(); console.log("Corte excluido com sucesso!"); this.buscarCortes()},
      error: (err) => {console.log(err)}
    })
  }

  cancelarCorte(corte : CorteResponse){

    this.corteService.cancelarCorte(corte.id).subscribe({
      next: (data) => {        
        this.buscarCortes(),        
        this.corteSelecionado = null;
        this.corteEdicao = null;
        this.idCortador = null;
        this.idEnfestador = null;
        this.opcoesCard = false},
      error: (err) => {console.log(err)}
    })
  }

  getMesAtualFormatado(): string {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    return `${ano}-${mes}`;
  }

  getNomeMesFormatado(mes: string): string {
    if (!mes) {
      return '';
    }

    const [ano, mesNumero] = mes.split('-');

    const nomesMeses = [
      'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
      'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
    ];

    const nomeMes = nomesMeses[Number(mesNumero) - 1];

    return `${nomeMes} de ${ano}`;
  }

  getquantidadeDeCortes(): number {
    return this.cortesBuscados.length;
  }
}
