package com.PedroNunesDev.Controle_de_Corte.dto.response;

public record AnaliseQuantidadePecasCorte(
        Long quantidadeTotal,
        Long quantidadeCortado,
        Long quantidadeEnfestaodo,
        Long quantidadePendente,
        Long quantidadeCancelado
) {
}
