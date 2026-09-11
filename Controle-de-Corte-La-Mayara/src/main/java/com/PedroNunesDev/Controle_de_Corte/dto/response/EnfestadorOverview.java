package com.PedroNunesDev.Controle_de_Corte.dto.response;

import java.io.Serializable;

public record EnfestadorOverview(
        Long idEnfestador,
        String nome,
        Long quantidadeDeCortes,
        Boolean isAtivo
) implements Serializable {
}
