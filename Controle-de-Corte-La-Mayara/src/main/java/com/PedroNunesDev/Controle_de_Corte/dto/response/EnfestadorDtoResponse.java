package com.PedroNunesDev.Controle_de_Corte.dto.response;

import java.io.Serializable;

public record EnfestadorDtoResponse (
        Long id,
        String nome
) implements Serializable {
}
