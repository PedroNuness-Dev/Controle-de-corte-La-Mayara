import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CortadorResponse } from '../../interfaces/cortador/CortadorResponse';
import { Observable } from 'rxjs';
import { CortadorRequest } from '../../interfaces/cortador/CortadorRequest';
import { CortadorOverview } from '../../interfaces/cortador/CortadorOverview';

@Injectable({
  providedIn: 'root',
})
export class CortadorService {

  http = inject(HttpClient);
  url = `http://${window.location.hostname}:8080/cortador`

  buscarCortadores() : Observable<CortadorResponse[]>{
    return this.http.get<CortadorResponse[]>(`${this.url}`)
  }

  buscarDetalhesCortadores() : Observable<CortadorOverview[]>{
      return this.http.get<CortadorOverview[]>(`${this.url}/overview`);
    }

  cadastrarCortador(cortador : CortadorRequest) : Observable<CortadorResponse>{
    return this.http.post<CortadorResponse>(`${this.url}`, cortador);
  }

  deletarCortador(id : number | null) : Observable<void>{
    return this.http.patch<void>(`${this.url}/${id}`, null);
  }
}
