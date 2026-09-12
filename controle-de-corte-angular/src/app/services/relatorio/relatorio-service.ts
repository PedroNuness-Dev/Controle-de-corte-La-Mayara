import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { QuantidadeDeCortes } from '../../interfaces/corte/QuantidadeDeCortesResponse';

@Injectable({
  providedIn: 'root',
})
export class RelatorioService {

  http = inject(HttpClient)

  url = `http://${window.location.hostname}:8080/relatorio/quantidade/registros`

  buscarQuantidadeDeCortesDoMes(mes:number,ano:number) : Observable<QuantidadeDeCortes>{
    return this.http.get<QuantidadeDeCortes>(`${this.url}?mes=${mes}&ano=${ano}`);
  }
}
