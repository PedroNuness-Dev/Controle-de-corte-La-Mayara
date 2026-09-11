export interface CorteUpdateRequest {
  dataDeCorte: string | null;
  nomeModelo: string;
  quantidadeTotal: number;
  observacao?: string;
  idCortador?: number | null;
  idEnfestador?: number | null;
  loteFormatado: string;
}