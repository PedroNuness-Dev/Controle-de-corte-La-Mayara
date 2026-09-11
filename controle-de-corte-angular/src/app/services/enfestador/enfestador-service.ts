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
  url = "http://localhost:8080/enfestador"

  buscarEnfestadores() : Observable<EnfestadorResponse[]>{
    return this.http.get<EnfestadorResponse[]>(`${this.url}`)
  }

  buscarDetalhesEnfestadores() : Observable<EnfestadorOverview[]>{
    return this.http.get<EnfestadorOverview[]>(`${this.url}/overview`);
  }

  cadastrarEnfestador(enfestador : EnfestadorRequest) : Observable<EnfestadorResponse>{
    return this.http.post<EnfestadorResponse>(`${this.url}`, enfestador);
  }

  deletarEnfestador(id : number | null){
    return this.http.patch<void>(`${this.url}/${id}`, null)
  }
}
