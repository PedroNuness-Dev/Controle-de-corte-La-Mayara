import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SuporteDtoRequest } from '../../interfaces/suporte/SuporteDtoRequest';

@Injectable({
  providedIn: 'root',
})
export class SuporteService {

  http = inject(HttpClient);
  url = "http://localhost:8080/email"

  mandarEmail(suporteDtoRequest : SuporteDtoRequest) : Observable<string>{
    return this.http.post(`${this.url}/send`,suporteDtoRequest, {responseType:'text'});
  }
}
