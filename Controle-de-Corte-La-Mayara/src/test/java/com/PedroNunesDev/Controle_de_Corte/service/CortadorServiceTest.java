package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.request.CortadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview;
import com.PedroNunesDev.Controle_de_Corte.exception.ResourceNotFoundException;
import com.PedroNunesDev.Controle_de_Corte.model.Cortador;
import com.PedroNunesDev.Controle_de_Corte.repository.CortadorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CortadorServiceTest {

    @Mock
    private CortadorRepository cortadorRepository;

    @InjectMocks
    private CortadorService cortadorService;

    @Test
    void deveBuscarCortadorPorIdComSucesso() {
        Cortador cortador = mock(Cortador.class);
        when(cortador.getId()).thenReturn(1L);
        when(cortador.getNome()).thenReturn("João");

        when(cortadorRepository.findById(1L))
                .thenReturn(Optional.of(cortador));

        CortadorDtoResponse response = cortadorService.buscarCortadorPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João", response.nome());

        verify(cortadorRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCortadorNaoExistirAoBuscarPorId() {
        when(cortadorRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cortadorService.buscarCortadorPorId(99L)
        );

        assertEquals("Cortador com ID 99 não encontrado", exception.getMessage());
    }

    @Test
    void deveRetornarListaDeCortadoresAtivos() {
        Cortador cortador1 = mock(Cortador.class);
        when(cortador1.getId()).thenReturn(1L);
        when(cortador1.getNome()).thenReturn("João");

        Cortador cortador2 = mock(Cortador.class);
        when(cortador2.getId()).thenReturn(2L);
        when(cortador2.getNome()).thenReturn("Maria");

        when(cortadorRepository.buscarCortadoresAtivos())
                .thenReturn(List.of(cortador1, cortador2));

        List<CortadorDtoResponse> response = cortadorService.buscarCortadoresAtivos();

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals("João", response.get(0).nome());
        assertEquals(2L, response.get(1).id());
        assertEquals("Maria", response.get(1).nome());

        verify(cortadorRepository).buscarCortadoresAtivos();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverCortadoresAtivos() {
        when(cortadorRepository.buscarCortadoresAtivos())
                .thenReturn(List.of());

        List<CortadorDtoResponse> response = cortadorService.buscarCortadoresAtivos();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveRetornarDetalhesDosCortadoresInativos() {
        List<CortadorOverview> overviews = List.of(mock(CortadorOverview.class));

        when(cortadorRepository.buscarCortadoresInativosESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<CortadorOverview> response = cortadorService.buscarDetalhesDosCortadoresInativos();

        assertSame(overviews, response);

        verify(cortadorRepository).buscarCortadoresInativosESuasQuantidadesDeCortes();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverCortadoresInativos() {
        when(cortadorRepository.buscarCortadoresInativosESuasQuantidadesDeCortes())
                .thenReturn(List.of());

        List<CortadorOverview> response = cortadorService.buscarDetalhesDosCortadoresInativos();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveBuscarDetalhesSemFiltroQuandoAmbasAsDatasForemNulas() {
        List<CortadorOverview> overviews = List.of(mock(CortadorOverview.class));

        when(cortadorRepository.buscarCortadoresESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<CortadorOverview> response = cortadorService.buscarDetalhesDosCortadoresAtivos(null, null);

        assertSame(overviews, response);

        verify(cortadorRepository).buscarCortadoresESuasQuantidadesDeCortes();
        verify(cortadorRepository, never()).buscarCortadoresESuasQuantidadesDeCortesPorData(any(), any());
    }

    @Test
    void deveBuscarDetalhesSemFiltroQuandoApenasDataInicialForNula() {
        List<CortadorOverview> overviews = List.of(mock(CortadorOverview.class));

        when(cortadorRepository.buscarCortadoresESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<CortadorOverview> response =
                cortadorService.buscarDetalhesDosCortadoresAtivos(null, LocalDate.of(2025, 12, 31));

        assertSame(overviews, response);

        verify(cortadorRepository).buscarCortadoresESuasQuantidadesDeCortes();
        verify(cortadorRepository, never()).buscarCortadoresESuasQuantidadesDeCortesPorData(any(), any());
    }

    @Test
    void deveBuscarDetalhesSemFiltroQuandoApenasDataFinalForNula() {
        List<CortadorOverview> overviews = List.of(mock(CortadorOverview.class));

        when(cortadorRepository.buscarCortadoresESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<CortadorOverview> response =
                cortadorService.buscarDetalhesDosCortadoresAtivos(LocalDate.of(2025, 1, 1), null);

        assertSame(overviews, response);

        verify(cortadorRepository).buscarCortadoresESuasQuantidadesDeCortes();
        verify(cortadorRepository, never()).buscarCortadoresESuasQuantidadesDeCortesPorData(any(), any());
    }

    @Test
    void deveBuscarDetalhesPorDataQuandoAmbasAsDatasForemInformadas() {
        LocalDate dataInicial = LocalDate.of(2025, 1, 1);
        LocalDate dataFinal = LocalDate.of(2025, 12, 31);

        List<CortadorOverview> overviews = List.of(mock(CortadorOverview.class));

        when(cortadorRepository.buscarCortadoresESuasQuantidadesDeCortesPorData(dataInicial, dataFinal))
                .thenReturn(overviews);

        List<CortadorOverview> response =
                cortadorService.buscarDetalhesDosCortadoresAtivos(dataInicial, dataFinal);

        assertSame(overviews, response);

        verify(cortadorRepository).buscarCortadoresESuasQuantidadesDeCortesPorData(dataInicial, dataFinal);
        verify(cortadorRepository, never()).buscarCortadoresESuasQuantidadesDeCortes();
    }

    @Test
    void devePermitirBuscaPorDataQuandoDataInicialForIgualADataFinal() {
        LocalDate data = LocalDate.of(2025, 6, 15);

        List<CortadorOverview> overviews = List.of(mock(CortadorOverview.class));

        when(cortadorRepository.buscarCortadoresESuasQuantidadesDeCortesPorData(data, data))
                .thenReturn(overviews);

        List<CortadorOverview> response =
                cortadorService.buscarDetalhesDosCortadoresAtivos(data, data);

        assertSame(overviews, response);

        verify(cortadorRepository).buscarCortadoresESuasQuantidadesDeCortesPorData(data, data);
    }

    @Test
    void deveLancarExcecaoQuandoDataInicialForPosteriorADataFinal() {
        LocalDate dataInicial = LocalDate.of(2025, 12, 31);
        LocalDate dataFinal = LocalDate.of(2025, 1, 1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cortadorService.buscarDetalhesDosCortadoresAtivos(dataInicial, dataFinal)
        );

        assertEquals("Data inicial não pode ser posterior a data final", exception.getMessage());

        verifyNoInteractions(cortadorRepository);
    }

    @Test
    void deveCadastrarCortadorComSucesso() {
        CortadorDtoRequest request = new CortadorDtoRequest("João");

        Cortador cortadorSalvo = mock(Cortador.class);
        when(cortadorSalvo.getId()).thenReturn(1L);
        when(cortadorSalvo.getNome()).thenReturn("João");

        when(cortadorRepository.save(any(Cortador.class)))
                .thenReturn(cortadorSalvo);

        CortadorDtoResponse response = cortadorService.cadastrarCortador(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João", response.nome());

        verify(cortadorRepository).save(any(Cortador.class));
    }

    @Test
    void deveCadastrarCortadorAtivoComONomeInformado() {
        CortadorDtoRequest request = new CortadorDtoRequest("Maria");

        Cortador cortadorSalvo = mock(Cortador.class);
        when(cortadorSalvo.getId()).thenReturn(2L);
        when(cortadorSalvo.getNome()).thenReturn("Maria");

        when(cortadorRepository.save(any(Cortador.class)))
                .thenReturn(cortadorSalvo);

        cortadorService.cadastrarCortador(request);

        ArgumentCaptor<Cortador> captor = ArgumentCaptor.forClass(Cortador.class);
        verify(cortadorRepository).save(captor.capture());

        Cortador cortadorCapturado = captor.getValue();

        assertEquals("Maria", cortadorCapturado.getNome());
        assertTrue(cortadorCapturado.getAtivo());
    }


    @Test
    void deveAtualizarCortadorComSucesso() {
        CortadorDtoRequest request = new CortadorDtoRequest("João Atualizado");

        Cortador cortador = criarCortadorMockParaOverview(1L, "João Atualizado", 5, true);

        when(cortadorRepository.findById(1L))
                .thenReturn(Optional.of(cortador));
        when(cortadorRepository.save(cortador))
                .thenReturn(cortador);

        CortadorOverview response = cortadorService.atualizarCortador(1L, request);

        assertEquals(new CortadorOverview(1L, "João Atualizado", 5L, true, null), response);

        verify(cortador).setNome("João Atualizado");
        verify(cortadorRepository).save(cortador);
    }

    @Test
    void deveLancarExcecaoQuandoCortadorNaoExistirAoAtualizar() {
        CortadorDtoRequest request = new CortadorDtoRequest("João Atualizado");

        when(cortadorRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cortadorService.atualizarCortador(99L, request)
        );

        assertEquals("Cortador com ID 99 não encontrado", exception.getMessage());

        verify(cortadorRepository, never()).save(any());
    }


    @Test
    void deveAtivarCortadorComSucesso() {
        Cortador cortador = criarCortadorMockParaOverview(1L, "João", 3, true);

        when(cortadorRepository.findById(1L))
                .thenReturn(Optional.of(cortador));

        CortadorOverview response = cortadorService.ativarCortador(1L);

        assertEquals(new CortadorOverview(1L, "João", 3L, true, null), response);

        verify(cortador).setAtivo(true);
        verify(cortadorRepository).save(cortador);
    }

    @Test
    void deveLancarExcecaoQuandoCortadorNaoExistirAoAtivar() {
        when(cortadorRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cortadorService.ativarCortador(99L)
        );

        assertEquals("Cortador com ID: 99 não encontrado", exception.getMessage());

        verify(cortadorRepository, never()).save(any());
    }


    @Test
    void deveDesativarCortadorComSucesso() {
        Cortador cortador = criarCortadorMockParaOverview(1L, "João", 3, false);

        when(cortadorRepository.findById(1L))
                .thenReturn(Optional.of(cortador));

        CortadorOverview response = cortadorService.desativarCortador(1L);

        assertEquals(new CortadorOverview(1L, "João", 3L, false, null), response);

        verify(cortador).setAtivo(false);
        verify(cortadorRepository).save(cortador);
    }

    @Test
    void deveLancarExcecaoQuandoCortadorNaoExistirAoDesativar() {
        when(cortadorRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cortadorService.desativarCortador(99L)
        );

        assertEquals("Cortador com ID: 99 não encontrado", exception.getMessage());

        verify(cortadorRepository, never()).save(any());
    }

    private Cortador criarCortadorMockParaOverview(Long id, String nome, int quantidadeDeCortes, Boolean ativo) {
        Cortador cortador = mock(Cortador.class);

        when(cortador.getId()).thenReturn(id);
        when(cortador.getNome()).thenReturn(nome);
        when(cortador.getQuantidadeDeCortes()).thenReturn(quantidadeDeCortes);
        when(cortador.getAtivo()).thenReturn(ativo);

        return cortador;
    }
}