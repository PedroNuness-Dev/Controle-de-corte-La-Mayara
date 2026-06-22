package com.PedroNunesDev.Controle_de_Corte.dto.request;

import java.time.LocalDate;

public record CorteDtoRequest (
        LocalDate dataDeRegistro,
        String nomeModelo,
        Integer quantidadeTotal,
        String observacao
){
}
