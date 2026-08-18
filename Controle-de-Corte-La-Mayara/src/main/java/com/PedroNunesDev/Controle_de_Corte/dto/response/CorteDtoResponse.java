package com.PedroNunesDev.Controle_de_Corte.dto.response;

import com.PedroNunesDev.Controle_de_Corte.enums.CorteStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

public record CorteDtoResponse (
        Long id,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataDeCorte,
        String nomeModelo,
        Integer quantidadeTotal,
        CorteStatus corteStatus,
        String loteFormatado,
        LoteDtoResponse lote,
        EnfestadorDtoResponse enfestador,
        CortadorDtoResponse cortador,
        String observacao
) implements Serializable{
}
