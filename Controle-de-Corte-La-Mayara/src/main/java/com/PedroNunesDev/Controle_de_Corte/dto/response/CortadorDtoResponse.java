package com.PedroNunesDev.Controle_de_Corte.dto.response;

import java.io.Serializable;

public record CortadorDtoResponse(
        Long id,
        String nome
) implements Serializable {
}
