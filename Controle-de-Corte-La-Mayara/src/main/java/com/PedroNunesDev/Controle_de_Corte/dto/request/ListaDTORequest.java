package com.PedroNunesDev.Controle_de_Corte.dto.request;

import java.util.List;

public record ListaDTORequest(
        String titulo,
        String descricao,
        List<ItemDTORequest> items
) {
}
