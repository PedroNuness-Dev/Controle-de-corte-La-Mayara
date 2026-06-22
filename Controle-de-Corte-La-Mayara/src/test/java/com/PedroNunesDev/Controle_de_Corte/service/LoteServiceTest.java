package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.response.LoteDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.model.Lote;
import com.PedroNunesDev.Controle_de_Corte.repository.LoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private LoteService loteService;

    @Test
    void deveRetornarLoteExistente() {
        int anoAtual = LocalDate.now().getYear();

        Lote lote = new Lote(1L, 5, anoAtual);

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.of(lote));

        LoteDtoResponse response = loteService.buscarLote();

        assertNotNull(response);
        assertEquals("5/" + anoAtual, response.numero_lote());
        assertEquals(anoAtual, response.ano());

        verify(loteRepository, never()).save(any());
    }

    @Test
    void deveCriarNovoLoteQuandoNaoExistir() {
        int anoAtual = LocalDate.now().getYear();

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.empty());

        Lote loteSalvo = new Lote(1L, 1, anoAtual);

        when(loteRepository.save(any(Lote.class)))
                .thenReturn(loteSalvo);

        LoteDtoResponse response = loteService.buscarLote();

        assertNotNull(response);
        assertEquals("1/" + anoAtual, response.numero_lote());
        assertEquals(anoAtual, response.ano());

        verify(loteRepository).save(any(Lote.class));
    }
}