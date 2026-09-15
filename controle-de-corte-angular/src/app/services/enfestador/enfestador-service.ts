import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EnfestadorResponse } from '../../interfaces/enfestador/EnfestadorResponse';
import { EnfestadorRequest } from '../../interfaces/enfestador/EnfestadorRequest';
import { EnfestadorOverview } from '../../interfaces/enfestador/EnfestadorOverview';

@Injectable({
  providedIn: 'root',
})
export class EnfestadorService {

  http = inject(HttpClient);
  url = `http://${window.location.hostname}:8080/enfestador`

  buscarEnfestadores() : Observable<EnfestadorResponse[]>{
    return this.http.get<EnfestadorResponse[]>(`${this.url}`)
  }

  buscarDetalhesEnfestadores() : Observable<EnfestadorOverview[]>{
    return this.http.get<EnfestadorOverview[]>(`${this.url}/ativos/overview`);
  }

  buscarDetalhesEnfestadoresInativos() : Observable<EnfestadorOverview[]>{
    return this.http.get<EnfestadorOverview[]>(`${this.url}/inativos/overview`);
  }

  cadastrarEnfestador(enfestador : EnfestadorRequest) : Observable<EnfestadorResponse>{
    return this.http.post<EnfestadorResponse>(`${this.url}`, enfestador);
  }

  atualizarEnfestador(idEnfestador : number, enfestadorAtualizado : EnfestadorRequest) : Observable<EnfestadorOverview>{
      return this.http.put<EnfestadorOverview>(`${this.url}/${idEnfestador}`, enfestadorAtualizado);
    }

  deletarEnfestador(id : number | null){
    return this.http.patch<void>(`${this.url}/desativar/${id}`, null)
  }

  ativarEnfestador(id : number | null){
    return this.http.patch<void>(`${this.url}/ativar/${id}`, null)
  }
}
