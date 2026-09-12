import { CortadorResponse } from "../cortador/CortadorResponse";
import { EnfestadorResponse } from "../enfestador/EnfestadorResponse";
import { LoteResponse } from "../lote/LoteResponse";

export interface CorteResponse{

    id:number,
    dataDeCorte: string,
    dataDeRegistro: string,
    nomeModelo : string,
    quantidadeTotal : number,
    corteStatus : string,
    loteFormatado:string,
    lote : LoteResponse,
    enfestador : EnfestadorResponse | null,
    cortador : CortadorResponse | null,
    observacao : string
}