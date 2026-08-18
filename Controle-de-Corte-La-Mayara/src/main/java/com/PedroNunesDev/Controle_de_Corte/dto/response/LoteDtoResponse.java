package com.PedroNunesDev.Controle_de_Corte.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

public record LoteDtoResponse (
        Long id,
        String numero_lote,
        @JsonFormat(pattern = "dd/MM/yyyy")
        Integer ano
) implements Serializable {
}
