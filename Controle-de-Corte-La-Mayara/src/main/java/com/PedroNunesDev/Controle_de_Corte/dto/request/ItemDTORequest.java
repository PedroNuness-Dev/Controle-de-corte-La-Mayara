package com.PedroNunesDev.Controle_de_Corte.dto.request;

public record ItemDTORequest(
        String nome,
        Integer quantidade,
        String observacao,
        Boolean atencao,
        Integer posicao
) {
}
