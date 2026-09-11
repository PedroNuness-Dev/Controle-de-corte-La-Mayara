import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoteResponse } from '../../interfaces/lote/LoteResponse';

@Injectable({
  providedIn: 'root',
})
export class LoteService {

  url = "http://localhost:8080/lote";

  http = inject(HttpClient);

  buscarLote() : Observable<LoteResponse>{
    return this.http.get<LoteResponse>(`${this.url}/buscar`);
  }

  incrementarLote() : Observable<void>{
    return this.http.put<void>(`${this.url}/incrementar`,null);
  }
  decrementarLote() : Observable<void>{
    return this.http.put<void>(`${this.url}/decrementar`,null);
  }
}
