import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { QuantidadeDeCortes } from '../../interfaces/corte/QuantidadeDeCortesResponse';

@Injectable({
  providedIn: 'root',
})
export class RelatorioService {

  http = inject(HttpClient)

  url = "http://localhost:8080/relatorio/buscar/quantidade/registros"

  buscarRelatorioDoMes(mes:number,ano:number) : Observable<QuantidadeDeCortes>{
    return this.http.get<QuantidadeDeCortes>(`${this.url}?mes=${mes}&ano=${ano}`);
  }
}
