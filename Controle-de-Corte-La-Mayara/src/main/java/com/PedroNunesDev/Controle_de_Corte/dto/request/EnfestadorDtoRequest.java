package com.PedroNunesDev.Controle_de_Corte.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EnfestadorDtoRequest(
        @NotBlank(message = "Nome do enfestador obrigatório")
        String nome
) {
}

