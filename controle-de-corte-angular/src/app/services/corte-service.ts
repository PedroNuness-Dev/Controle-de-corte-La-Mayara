import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CorteResponse } from '../interfaces/CorteResponse';
import { BehaviorSubject, Observable } from 'rxjs';
import { CorteUpdateRequest } from '../interfaces/CorteUpdate';
import { CorteRequest } from '../interfaces/CorteRequest';
import { CortadorResponse } from '../interfaces/CortadorResponse';
import { EstatisticaPessoa } from '../interfaces/EstatisticasPessoa';

@Injectable({
  providedIn: 'root',
})
export class CorteService {

  url = "http://localhost:8080/corte"

  http = inject(HttpClient);

  buscarCortesDoMes(mes : number | null, ano : number | null) : Observable<CorteResponse[]>{

    if(mes != null && ano != null){

      return this.http.get<CorteResponse[]>(`${this.url}/buscar/mes?mes=${mes}&ano${ano}`)      
    }
    else{
      return this.http.get<CorteResponse[]>(`${this.url}/buscar/mes`)
    }
  }

  buscarCortesDoMesPorStatus(status:string, ano : number, mes : number) : Observable<CorteResponse[]>{

    return this.http.get<CorteResponse[]>(`${this.url}/buscar/mes/status?mes=${mes}&ano=${ano}&status=${status}`)
  }

  buscarRelatorio() : Observable<number>{
    return this.http.get<number>(`${this.url}/relatorio`);
  }

  atualizarCorte(idCorte: number, corteParaAtualizar: CorteUpdateRequest):Observable<CorteResponse>{

    return this.http.put<CorteResponse>(`${this.url}/atualizar/${idCorte}`,corteParaAtualizar);
  }

  buscarCortePorNomeOuLote(tipoBuscado : string, mes : number ,ano : number) : Observable<CorteResponse[]>{
    return this.http.get<CorteResponse[]>(`${this.url}/buscar?tipo=${tipoBuscado}&mes=${mes}&ano=${ano}`)
  }

  adicionarCorte(corte : CorteRequest) : Observable<CorteResponse>{
    return this.http.post<CorteResponse>(`${this.url}`, corte);
  }

  cancelarCorte(idCorte : number) : Observable<void>{
    return this.http.put<void>(`${this.url}/${idCorte}/cancelar`,null)
  }

  excluirCorte(id : number):Observable<void>{
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
