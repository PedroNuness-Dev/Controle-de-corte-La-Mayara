import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, from, Observable, switchMap, throwError } from 'rxjs';
import { QuantidadeDeCortes } from '../../interfaces/corte/QuantidadeDeCortesResponse';

@Injectable({
  providedIn: 'root',
})
export class RelatorioService {

  private http = inject(HttpClient);

  private url = `http://${window.location.hostname}:8080/relatorio`;

  buscarQuantidadeDeCortesDoMes(mes: number, ano: number): Observable<QuantidadeDeCortes> {
    const params = new HttpParams().set('mes', mes).set('ano', ano);
    return this.http.get<QuantidadeDeCortes>(`${this.url}/quantidade/registros`, { params });
  }

  /** Busca o PDF do relatório de cortes como Blob. */
  gerarRelatorioCortesPdf(dataInicial: Date | string, dataFinal: Date | string): Observable<Blob> {
    const params = new HttpParams()
      .set('dataInicial', this.formatarData(dataInicial))
      .set('dataFinal', this.formatarData(dataFinal));

    return this.http
      .get(`${this.url}/cortes/pdf`, { params, responseType: 'blob' })
      .pipe(catchError(erro => this.tratarErroBlob(erro)));
  }

  /** Download automático do Blob. */
  baixarArquivo(blob: Blob, nomeArquivo = 'relatorio-cortes.pdf'): void {
    const objectUrl = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = objectUrl;
    a.download = nomeArquivo;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(objectUrl);
  }

  /** Converte Date ou string em 'yyyy-MM-dd' sem sofrer com fuso (UTC). */
  private formatarData(data: Date | string): string {
    if (typeof data === 'string') return data.substring(0, 10);
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }

  /**
   * Com responseType 'blob', o corpo de um erro também chega como Blob.
   * Aqui lemos esse Blob como texto e extraímos a mensagem do backend.
   */
  private tratarErroBlob(erro: HttpErrorResponse): Observable<never> {
    if (erro.error instanceof Blob) {
      return from(erro.error.text()).pipe(
        switchMap(texto => {
          let mensagem = texto;
          try {
            const json = JSON.parse(texto);
            mensagem = json.message ?? json.mensagem ?? json.detail ?? texto;
          } catch { /* não era JSON, usa o texto cru */ }
          return throwError(() => new Error(mensagem || 'Erro ao gerar o relatório.'));
        })
      );
    }
    return throwError(() => erro);
  }
}