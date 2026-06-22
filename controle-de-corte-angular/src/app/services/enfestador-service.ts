import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CortadorResponse } from '../interfaces/CortadorResponse';
import { Observable } from 'rxjs';
import { EnfestadorResponse } from '../interfaces/EnfestadorResponse';
import { EnfestadorRequest } from '../interfaces/EnfestadorRequest';

@Injectable({
  providedIn: 'root',
})
export class EnfestadorService {

  http = inject(HttpClient);
  url = "http://localhost:8080/enfestador"

  buscarEnfestadores() : Observable<EnfestadorResponse[]>{
    return this.http.get<EnfestadorResponse[]>(`${this.url}`)
  }

  cadastrarEnfestador(enfestador : EnfestadorRequest) : Observable<EnfestadorResponse>{
    return this.http.post<EnfestadorResponse>(`${this.url}`, enfestador);
  }

  deletarEnfestador(id : number | null){
    return this.http.patch<void>(`${this.url}/${id}`, null)
  }
}
