import { CortadorResponse } from "./CortadorResponse";
import { EnfestadorResponse } from "./EnfestadorResponse";
import { LoteResponse } from "./LoteResponse";

export interface CorteResponse{

    id:number,
    dataDeCorte: string,
    nomeModelo : string,
    quantidadeTotal : number,
    corteStatus : string,
    loteFormatado:string,
    lote : LoteResponse,
    enfestador : EnfestadorResponse | null,
    cortador : CortadorResponse | null,
    observacao : string
}