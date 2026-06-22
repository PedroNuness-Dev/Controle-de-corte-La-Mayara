package com.PedroNunesDev.Controle_de_Corte.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CortadorDtoRequest (
        @NotBlank(message = "Nome do cortador obrigatório")
        String nome
){
}
