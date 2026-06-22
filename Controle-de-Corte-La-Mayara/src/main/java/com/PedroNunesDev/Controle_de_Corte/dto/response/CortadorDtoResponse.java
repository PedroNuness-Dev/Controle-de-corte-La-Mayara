package com.PedroNunesDev.Controle_de_Corte.dto.response;

public record CortadorDtoResponse(
        Long id,
        String nome,
        Integer quantidadeDeCortesCortados
) {
}
