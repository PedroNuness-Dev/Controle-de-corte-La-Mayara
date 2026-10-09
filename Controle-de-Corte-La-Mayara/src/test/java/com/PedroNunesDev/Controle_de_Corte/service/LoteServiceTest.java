package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.response.LoteDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.exception.InvalidOperationException;
import com.PedroNunesDev.Controle_de_Corte.exception.ResourceNotFoundException;
import com.PedroNunesDev.Controle_de_Corte.model.Lote;
import com.PedroNunesDev.Controle_de_Corte.repository.LoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Test
    void deveSalvarNovoLoteComSucesso() {
        int anoAtual = LocalDate.now().getYear();

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.empty());

        Lote loteSalvo = new Lote(1L, 1, anoAtual);

        when(loteRepository.save(any(Lote.class)))
                .thenReturn(loteSalvo);

        LoteDtoResponse response = loteService.salvarNovoLote();

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("1/" + anoAtual, response.numero_lote());
        assertEquals(anoAtual, response.ano());

        verify(loteRepository).save(any(Lote.class));
    }

    @Test
    void deveSalvarNovoLoteComNumeroUmEAnoAtual() {
        int anoAtual = LocalDate.now().getYear();

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.empty());

        when(loteRepository.save(any(Lote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        loteService.salvarNovoLote();

        ArgumentCaptor<Lote> captor = ArgumentCaptor.forClass(Lote.class);
        verify(loteRepository).save(captor.capture());

        Lote loteCapturado = captor.getValue();

        assertNull(loteCapturado.getId());
        assertEquals(1, loteCapturado.getNumero_lote());
        assertEquals(anoAtual, loteCapturado.getAno());
    }

    @Test
    void deveLancarExcecaoQuandoLoteJaCadastradoAoSalvarNovoLote() {
        int anoAtual = LocalDate.now().getYear();

        Lote loteExistente = new Lote(1L, 3, anoAtual);

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.of(loteExistente));

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> loteService.salvarNovoLote()
        );

        assertEquals("Lote já cadastrado no banco de dados", exception.getMessage());

        verify(loteRepository, never()).save(any());
    }


    @Test
    void deveRetornarLoteModelQuandoExistir() {
        int anoAtual = LocalDate.now().getYear();

        Lote lote = new Lote(1L, 7, anoAtual);

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.of(lote));

        Lote resultado = loteService.buscarLoteModel();

        assertNotNull(resultado);
        assertSame(lote, resultado);
        assertEquals(7, resultado.getNumero_lote());
        assertEquals(anoAtual, resultado.getAno());
    }

    @Test
    void deveLancarExcecaoQuandoLoteModelNaoExistir() {
        int anoAtual = LocalDate.now().getYear();

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> loteService.buscarLoteModel()
        );

        assertEquals("Lote não encontrado para o ano " + anoAtual, exception.getMessage());
    }

    @Test
    void deveIncrementarLoteComSucesso() {
        int anoAtual = LocalDate.now().getYear();

        Lote lote = new Lote(1L, 5, anoAtual);

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.of(lote));

        loteService.incrementarLote();

        assertEquals(6, lote.getNumero_lote());

        verify(loteRepository).save(lote);
    }

    @Test
    void deveLancarExcecaoAoIncrementarQuandoLoteNaoExistir() {
        int anoAtual = LocalDate.now().getYear();

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> loteService.incrementarLote()
        );

        assertEquals("Lote não encontrado para o ano " + anoAtual, exception.getMessage());

        verify(loteRepository, never()).save(any());
    }

    @Test
    void deveDecrementarLoteComSucesso() {
        int anoAtual = LocalDate.now().getYear();

        Lote lote = new Lote(1L, 5, anoAtual);

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.of(lote));

        loteService.decrementarLote();

        assertEquals(4, lote.getNumero_lote());

        verify(loteRepository).save(lote);
    }

    @Test
    void deveDecrementarLoteDeDoisParaUm() {
        int anoAtual = LocalDate.now().getYear();

        Lote lote = new Lote(1L, 2, anoAtual);

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.of(lote));

        loteService.decrementarLote();

        assertEquals(1, lote.getNumero_lote());

        verify(loteRepository).save(lote);
    }

    @Test
    void deveLancarExcecaoAoDecrementarLoteNoMinimo() {
        int anoAtual = LocalDate.now().getYear();

        Lote lote = new Lote(1L, 1, anoAtual);

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.of(lote));

        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> loteService.decrementarLote()
        );

        assertEquals("Não é permitido decrementar o lote abaixo de 1", exception.getMessage());
        assertEquals(1, lote.getNumero_lote());

        verify(loteRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoDecrementarLoteAbaixoDoMinimo() {
        int anoAtual = LocalDate.now().getYear();

        Lote lote = new Lote(1L, 0, anoAtual);

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.of(lote));

        assertThrows(
                InvalidOperationException.class,
                () -> loteService.decrementarLote()
        );

        verify(loteRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoDecrementarQuandoLoteNaoExistir() {
        int anoAtual = LocalDate.now().getYear();

        when(loteRepository.findByAno(anoAtual))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> loteService.decrementarLote()
        );

        assertEquals("Lote não encontrado para o ano " + anoAtual, exception.getMessage());

        verify(loteRepository, never()).save(any());
    }
}