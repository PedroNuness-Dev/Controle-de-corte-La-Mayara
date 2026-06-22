package com.PedroNunesDev.Controle_de_Corte.dto.response;

public record EstatisticaPessoaDtoResponse(
        String nome,
        Long quantidadeDeCortesEnfestados,
        Long quantidadeDeCortesCortados
) {
}
