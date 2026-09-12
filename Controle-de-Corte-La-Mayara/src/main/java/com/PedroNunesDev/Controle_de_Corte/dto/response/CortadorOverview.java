package com.PedroNunesDev.Controle_de_Corte.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

public record CortadorOverview(
        Long idCortador,
        String nome,
        Long quantidadeDeCortes,
        Boolean isAtivo,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataDeCadastro
) implements Serializable {
}
