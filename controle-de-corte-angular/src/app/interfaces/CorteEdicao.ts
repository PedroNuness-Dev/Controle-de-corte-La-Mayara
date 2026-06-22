export interface CorteEdicao {
  dataDeCorte: string;
  nomeModelo: string;
  quantidadeTotal: number;
  observacao: string;
  loteFormatado: string;

  idCortador: number | null;
  idEnfestador: number | null;
}