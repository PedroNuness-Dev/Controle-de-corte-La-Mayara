package com.PedroNunesDev.Controle_de_Corte.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record AnaliseCortesResponse(
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataInicial,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataFinal,
        QuantidadeCortesMesResponse quantidadesCortes,
        List<CortadorOverview> cortadores,
        List<EnfestadorOverview> enfestadores

) {
}
