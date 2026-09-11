package com.PedroNunesDev.Controle_de_Corte.dto.response;

import java.io.Serializable;

public record CortadorOverview(
        Long idCortador,
        String nome,
        Long quantidadeDeCortes,
        Boolean isAtivo
) implements Serializable {
}
