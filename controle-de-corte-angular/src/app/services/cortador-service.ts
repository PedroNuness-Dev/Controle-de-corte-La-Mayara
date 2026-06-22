import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CortadorResponse } from '../interfaces/CortadorResponse';
import { Observable } from 'rxjs';
import { CortadorRequest } from '../interfaces/CortadorRequest';

@Injectable({
  providedIn: 'root',
})
export class CortadorService {

  http = inject(HttpClient);
  url = "http://localhost:8080/cortador"

  buscarCortadores() : Observable<CortadorResponse[]>{
    return this.http.get<CortadorResponse[]>(`${this.url}`)
  }

  cadastrarCortador(cortador : CortadorRequest) : Observable<CortadorResponse>{
    return this.http.post<CortadorResponse>(`${this.url}`, cortador);
  }

  deletarCortador(id : number | null) : Observable<void>{
    return this.http.patch<void>(`${this.url}/${id}`, null);
  }
}
