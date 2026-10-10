package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.utils.ValidacaoDatas;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ValidacaoDatasTest {

    private final ValidacaoDatas validacaoDatas = new ValidacaoDatas();

    @Test
    void naoDeveLancarExcecaoQuandoDataInicialForAnteriorADataFinal() {
        assertDoesNotThrow(() ->
                validacaoDatas.validarDatas(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)));
    }

    @Test
    void naoDeveLancarExcecaoQuandoDataInicialForIgualADataFinal() {
        LocalDate data = LocalDate.of(2025, 6, 15);

        assertDoesNotThrow(() -> validacaoDatas.validarDatas(data, data));
    }

    @Test
    void deveLancarExcecaoQuandoDataInicialForPosteriorADataFinal() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validacaoDatas.validarDatas(LocalDate.of(2025, 12, 31), LocalDate.of(2025, 1, 1))
        );

        assertEquals("Data inicial não pode ser posterior a data final", exception.getMessage());
    }
}