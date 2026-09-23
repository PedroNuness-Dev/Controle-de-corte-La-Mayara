package com.PedroNunesDev.Controle_de_Corte.dto.response;

public record QuantidadeCortesMesResponse(
        Long quantidadeTotal,
        Long quantidadePendentes,
        Long quantidadeEnfestados,
        Long quantidadeCortados,
        Long quantidadeCancelados
) {
}
