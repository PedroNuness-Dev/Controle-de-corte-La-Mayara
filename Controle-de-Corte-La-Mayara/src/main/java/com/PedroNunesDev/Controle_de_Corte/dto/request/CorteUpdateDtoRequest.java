package com.PedroNunesDev.Controle_de_Corte.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CorteUpdateDtoRequest (
        LocalDate dataDeCorte,
        @NotBlank(message = "Nome do modelo obrigatório")
        String nomeModelo,
        @NotNull(message = "Quantidade total obrigatória")
        Integer quantidadeTotal,
        String observacao,
        Long idCortador,
        Long idEnfestador,
        @NotBlank(message = "Lote obrigatório")
        String loteFormatado
){
}
