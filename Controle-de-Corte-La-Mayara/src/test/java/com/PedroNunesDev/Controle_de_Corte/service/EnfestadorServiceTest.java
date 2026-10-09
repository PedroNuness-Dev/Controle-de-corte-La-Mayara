package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.request.EnfestadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorOverview;
import com.PedroNunesDev.Controle_de_Corte.exception.ResourceNotFoundException;
import com.PedroNunesDev.Controle_de_Corte.model.Enfestador;
import com.PedroNunesDev.Controle_de_Corte.repository.EnfestadorRepository;
import com.PedroNunesDev.Controle_de_Corte.utils.ValidacaoDatas;
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
class EnfestadorServiceTest {

    @Mock
    private EnfestadorRepository enfestadorRepository;

    @Mock
    private ValidacaoDatas validacaoDatas;

    @InjectMocks
    private EnfestadorService enfestadorService;

    // ------------------------------------------------------------------
    // buscarEnfestadorPorId
    // ------------------------------------------------------------------

    @Test
    void deveBuscarEnfestadorPorIdComSucesso() {
        Enfestador enfestador = mock(Enfestador.class);
        when(enfestador.getId()).thenReturn(1L);
        when(enfestador.getNome()).thenReturn("João");

        when(enfestadorRepository.findById(1L))
                .thenReturn(Optional.of(enfestador));

        EnfestadorDtoResponse response = enfestadorService.buscarEnfestadorPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João", response.nome());

        verify(enfestadorRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoEnfestadorNaoExistirAoBuscarPorId() {
        when(enfestadorRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> enfestadorService.buscarEnfestadorPorId(99L)
        );

        assertEquals("Enfestador com ID 99 não encontrado", exception.getMessage());
    }

    // ------------------------------------------------------------------
    // buscarEnfestadoresAtivos
    // ------------------------------------------------------------------

    @Test
    void deveRetornarListaDeEnfestadoresAtivos() {
        Enfestador enfestador1 = mock(Enfestador.class);
        when(enfestador1.getId()).thenReturn(1L);
        when(enfestador1.getNome()).thenReturn("João");

        Enfestador enfestador2 = mock(Enfestador.class);
        when(enfestador2.getId()).thenReturn(2L);
        when(enfestador2.getNome()).thenReturn("Maria");

        when(enfestadorRepository.buscarEnfestadoresAtivos())
                .thenReturn(List.of(enfestador1, enfestador2));

        List<EnfestadorDtoResponse> response = enfestadorService.buscarEnfestadoresAtivos();

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals("João", response.get(0).nome());
        assertEquals(2L, response.get(1).id());
        assertEquals("Maria", response.get(1).nome());

        verify(enfestadorRepository).buscarEnfestadoresAtivos();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverEnfestadoresAtivos() {
        when(enfestadorRepository.buscarEnfestadoresAtivos())
                .thenReturn(List.of());

        List<EnfestadorDtoResponse> response = enfestadorService.buscarEnfestadoresAtivos();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    // ------------------------------------------------------------------
    // buscarDetalhesDosEnfestadoresAtivos
    // ------------------------------------------------------------------

    @Test
    void deveBuscarDetalhesSemFiltroQuandoAmbasAsDatasForemNulas() {
        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<EnfestadorOverview> response = enfestadorService.buscarDetalhesDosEnfestadoresAtivos(null, null);

        assertSame(overviews, response);

        verify(enfestadorRepository).buscarEnfestadoresESuasQuantidadesDeCortes();
        verify(enfestadorRepository, never()).buscarEnfestadoresESuasQuantidadesDeCortesPorData(any(), any());
        verifyNoInteractions(validacaoDatas);
    }

    @Test
    void deveBuscarDetalhesSemFiltroQuandoApenasDataInicialForNula() {
        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<EnfestadorOverview> response =
                enfestadorService.buscarDetalhesDosEnfestadoresAtivos(null, LocalDate.of(2025, 12, 31));

        assertSame(overviews, response);

        verify(enfestadorRepository).buscarEnfestadoresESuasQuantidadesDeCortes();
        verify(enfestadorRepository, never()).buscarEnfestadoresESuasQuantidadesDeCortesPorData(any(), any());
        verifyNoInteractions(validacaoDatas);
    }

    @Test
    void deveBuscarDetalhesSemFiltroQuandoApenasDataFinalForNula() {
        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<EnfestadorOverview> response =
                enfestadorService.buscarDetalhesDosEnfestadoresAtivos(LocalDate.of(2025, 1, 1), null);

        assertSame(overviews, response);

        verify(enfestadorRepository).buscarEnfestadoresESuasQuantidadesDeCortes();
        verify(enfestadorRepository, never()).buscarEnfestadoresESuasQuantidadesDeCortesPorData(any(), any());
        verifyNoInteractions(validacaoDatas);
    }

    @Test
    void deveBuscarDetalhesPorDataQuandoAmbasAsDatasForemInformadas() {
        LocalDate dataInicial = LocalDate.of(2025, 1, 1);
        LocalDate dataFinal = LocalDate.of(2025, 12, 31);

        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresESuasQuantidadesDeCortesPorData(dataInicial, dataFinal))
                .thenReturn(overviews);

        List<EnfestadorOverview> response =
                enfestadorService.buscarDetalhesDosEnfestadoresAtivos(dataInicial, dataFinal);

        assertSame(overviews, response);

        verify(validacaoDatas).validarDatas(dataInicial, dataFinal);
        verify(enfestadorRepository).buscarEnfestadoresESuasQuantidadesDeCortesPorData(dataInicial, dataFinal);
        verify(enfestadorRepository, never()).buscarEnfestadoresESuasQuantidadesDeCortes();
    }

    @Test
    void devePermitirBuscaPorDataQuandoDataInicialForIgualADataFinal() {
        LocalDate data = LocalDate.of(2025, 6, 15);

        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresESuasQuantidadesDeCortesPorData(data, data))
                .thenReturn(overviews);

        List<EnfestadorOverview> response =
                enfestadorService.buscarDetalhesDosEnfestadoresAtivos(data, data);

        assertSame(overviews, response);

        verify(validacaoDatas).validarDatas(data, data);
        verify(enfestadorRepository).buscarEnfestadoresESuasQuantidadesDeCortesPorData(data, data);
    }

    @Test
    void deveLancarExcecaoQuandoDataInicialForPosteriorADataFinal() {
        LocalDate dataInicial = LocalDate.of(2025, 12, 31);
        LocalDate dataFinal = LocalDate.of(2025, 1, 1);

        doThrow(new IllegalArgumentException("Data inicial não pode ser posterior a data final"))
                .when(validacaoDatas).validarDatas(dataInicial, dataFinal);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> enfestadorService.buscarDetalhesDosEnfestadoresAtivos(dataInicial, dataFinal)
        );

        assertEquals("Data inicial não pode ser posterior a data final", exception.getMessage());

        verify(validacaoDatas).validarDatas(dataInicial, dataFinal);
        verifyNoInteractions(enfestadorRepository);
    }

    // ------------------------------------------------------------------
    // buscarDetalhesDosEnfestadoresInativos
    // ------------------------------------------------------------------

    @Test
    void deveBuscarDetalhesInativosSemFiltroQuandoAmbasAsDatasForemNulas() {
        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresInativosESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<EnfestadorOverview> response = enfestadorService.buscarDetalhesDosEnfestadoresInativos(null, null);

        assertSame(overviews, response);

        verify(enfestadorRepository).buscarEnfestadoresInativosESuasQuantidadesDeCortes();
        verify(enfestadorRepository, never()).buscarEnfestadoresInativosESuasQuantidadesDeCortesPorData(any(), any());
        verifyNoInteractions(validacaoDatas);
    }

    @Test
    void deveBuscarDetalhesInativosSemFiltroQuandoApenasDataInicialForNula() {
        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresInativosESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<EnfestadorOverview> response =
                enfestadorService.buscarDetalhesDosEnfestadoresInativos(null, LocalDate.of(2025, 12, 31));

        assertSame(overviews, response);

        verify(enfestadorRepository).buscarEnfestadoresInativosESuasQuantidadesDeCortes();
        verify(enfestadorRepository, never()).buscarEnfestadoresInativosESuasQuantidadesDeCortesPorData(any(), any());
        verifyNoInteractions(validacaoDatas);
    }

    @Test
    void deveBuscarDetalhesInativosSemFiltroQuandoApenasDataFinalForNula() {
        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresInativosESuasQuantidadesDeCortes())
                .thenReturn(overviews);

        List<EnfestadorOverview> response =
                enfestadorService.buscarDetalhesDosEnfestadoresInativos(LocalDate.of(2025, 1, 1), null);

        assertSame(overviews, response);

        verify(enfestadorRepository).buscarEnfestadoresInativosESuasQuantidadesDeCortes();
        verify(enfestadorRepository, never()).buscarEnfestadoresInativosESuasQuantidadesDeCortesPorData(any(), any());
        verifyNoInteractions(validacaoDatas);
    }

    @Test
    void deveBuscarDetalhesInativosPorDataQuandoAmbasAsDatasForemInformadas() {
        LocalDate dataInicial = LocalDate.of(2025, 1, 1);
        LocalDate dataFinal = LocalDate.of(2025, 12, 31);

        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresInativosESuasQuantidadesDeCortesPorData(dataInicial, dataFinal))
                .thenReturn(overviews);

        List<EnfestadorOverview> response =
                enfestadorService.buscarDetalhesDosEnfestadoresInativos(dataInicial, dataFinal);

        assertSame(overviews, response);

        verify(validacaoDatas).validarDatas(dataInicial, dataFinal);
        verify(enfestadorRepository).buscarEnfestadoresInativosESuasQuantidadesDeCortesPorData(dataInicial, dataFinal);
        verify(enfestadorRepository, never()).buscarEnfestadoresInativosESuasQuantidadesDeCortes();
    }

    @Test
    void devePermitirBuscaDeInativosPorDataQuandoDataInicialForIgualADataFinal() {
        LocalDate data = LocalDate.of(2025, 6, 15);

        List<EnfestadorOverview> overviews = List.of(mock(EnfestadorOverview.class));

        when(enfestadorRepository.buscarEnfestadoresInativosESuasQuantidadesDeCortesPorData(data, data))
                .thenReturn(overviews);

        List<EnfestadorOverview> response =
                enfestadorService.buscarDetalhesDosEnfestadoresInativos(data, data);

        assertSame(overviews, response);

        verify(validacaoDatas).validarDatas(data, data);
        verify(enfestadorRepository).buscarEnfestadoresInativosESuasQuantidadesDeCortesPorData(data, data);
    }

    @Test
    void deveLancarExcecaoAoBuscarInativosQuandoDataInicialForPosteriorADataFinal() {
        LocalDate dataInicial = LocalDate.of(2025, 12, 31);
        LocalDate dataFinal = LocalDate.of(2025, 1, 1);

        doThrow(new IllegalArgumentException("Data inicial não pode ser posterior a data final"))
                .when(validacaoDatas).validarDatas(dataInicial, dataFinal);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> enfestadorService.buscarDetalhesDosEnfestadoresInativos(dataInicial, dataFinal)
        );

        assertEquals("Data inicial não pode ser posterior a data final", exception.getMessage());

        verify(validacaoDatas).validarDatas(dataInicial, dataFinal);
        verifyNoInteractions(enfestadorRepository);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverEnfestadoresInativosSemFiltro() {
        when(enfestadorRepository.buscarEnfestadoresInativosESuasQuantidadesDeCortes())
                .thenReturn(List.of());

        List<EnfestadorOverview> response = enfestadorService.buscarDetalhesDosEnfestadoresInativos(null, null);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverEnfestadoresInativosNoPeriodo() {
        LocalDate dataInicial = LocalDate.of(2025, 1, 1);
        LocalDate dataFinal = LocalDate.of(2025, 12, 31);

        when(enfestadorRepository.buscarEnfestadoresInativosESuasQuantidadesDeCortesPorData(dataInicial, dataFinal))
                .thenReturn(List.of());

        List<EnfestadorOverview> response =
                enfestadorService.buscarDetalhesDosEnfestadoresInativos(dataInicial, dataFinal);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    // ------------------------------------------------------------------
    // cadastrarEnfestador
    // ------------------------------------------------------------------

    @Test
    void deveCadastrarEnfestadorComSucesso() {
        EnfestadorDtoRequest request = new EnfestadorDtoRequest("João");

        Enfestador enfestadorSalvo = mock(Enfestador.class);
        when(enfestadorSalvo.getId()).thenReturn(1L);
        when(enfestadorSalvo.getNome()).thenReturn("João");

        when(enfestadorRepository.save(any(Enfestador.class)))
                .thenReturn(enfestadorSalvo);

        EnfestadorDtoResponse response = enfestadorService.cadastrarEnfestador(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João", response.nome());

        verify(enfestadorRepository).save(any(Enfestador.class));
    }

    @Test
    void deveCadastrarEnfestadorAtivoComONomeInformado() {
        EnfestadorDtoRequest request = new EnfestadorDtoRequest("Maria");

        Enfestador enfestadorSalvo = mock(Enfestador.class);
        when(enfestadorSalvo.getId()).thenReturn(2L);
        when(enfestadorSalvo.getNome()).thenReturn("Maria");

        when(enfestadorRepository.save(any(Enfestador.class)))
                .thenReturn(enfestadorSalvo);

        enfestadorService.cadastrarEnfestador(request);

        ArgumentCaptor<Enfestador> captor = ArgumentCaptor.forClass(Enfestador.class);
        verify(enfestadorRepository).save(captor.capture());

        Enfestador enfestadorCapturado = captor.getValue();

        assertEquals("Maria", enfestadorCapturado.getNome());
        assertTrue(enfestadorCapturado.getAtivo());
    }

    // ------------------------------------------------------------------
    // atualizarEnfestador
    // ------------------------------------------------------------------

    @Test
    void deveAtualizarEnfestadorComSucesso() {
        EnfestadorDtoRequest request = new EnfestadorDtoRequest("João Atualizado");

        Enfestador enfestador = criarEnfestadorMockParaOverview(1L, "João Atualizado", 5, true);

        when(enfestadorRepository.findById(1L))
                .thenReturn(Optional.of(enfestador));
        when(enfestadorRepository.save(enfestador))
                .thenReturn(enfestador);

        EnfestadorOverview response = enfestadorService.atualizarEnfestador(1L, request);

        assertEquals(new EnfestadorOverview(1L, "João Atualizado", 5L, true, null), response);

        verify(enfestador).setNome("João Atualizado");
        verify(enfestadorRepository).save(enfestador);
    }

    @Test
    void deveLancarExcecaoQuandoEnfestadorNaoExistirAoAtualizar() {
        EnfestadorDtoRequest request = new EnfestadorDtoRequest("João Atualizado");

        when(enfestadorRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> enfestadorService.atualizarEnfestador(99L, request)
        );

        assertEquals("Enfestador com ID 99 não encontrado", exception.getMessage());

        verify(enfestadorRepository, never()).save(any());
    }

    // ------------------------------------------------------------------
    // desativarEnfestador
    // ------------------------------------------------------------------

    @Test
    void deveDesativarEnfestadorComSucesso() {
        Enfestador enfestador = criarEnfestadorMockParaOverview(1L, "João", 3, false);

        when(enfestadorRepository.findById(1L))
                .thenReturn(Optional.of(enfestador));

        EnfestadorOverview response = enfestadorService.desativarEnfestador(1L);

        assertEquals(new EnfestadorOverview(1L, "João", 3L, false, null), response);

        verify(enfestador).setAtivo(false);
        verify(enfestadorRepository).save(enfestador);
    }

    @Test
    void deveLancarExcecaoQuandoEnfestadorNaoExistirAoDesativar() {
        when(enfestadorRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> enfestadorService.desativarEnfestador(99L)
        );

        assertEquals("Enfestador com ID: 99 não encontrado", exception.getMessage());

        verify(enfestadorRepository, never()).save(any());
    }

    // ------------------------------------------------------------------
    // ativarEnfestador
    // ------------------------------------------------------------------

    @Test
    void deveAtivarEnfestadorComSucesso() {
        Enfestador enfestador = criarEnfestadorMockParaOverview(1L, "João", 3, true);

        when(enfestadorRepository.findById(1L))
                .thenReturn(Optional.of(enfestador));

        EnfestadorOverview response = enfestadorService.ativarEnfestador(1L);

        assertEquals(new EnfestadorOverview(1L, "João", 3L, true, null), response);

        verify(enfestador).setAtivo(true);
        verify(enfestadorRepository).save(enfestador);
    }

    @Test
    void deveLancarExcecaoQuandoEnfestadorNaoExistirAoAtivar() {
        when(enfestadorRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> enfestadorService.ativarEnfestador(99L)
        );

        assertEquals("Enfestador com ID: 99 não encontrado", exception.getMessage());

        verify(enfestadorRepository, never()).save(any());
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private Enfestador criarEnfestadorMockParaOverview(Long id, String nome, int quantidadeDeCortesEnfestados, Boolean ativo) {
        Enfestador enfestador = mock(Enfestador.class);

        when(enfestador.getId()).thenReturn(id);
        when(enfestador.getNome()).thenReturn(nome);
        when(enfestador.getQuantidadeDeCortesEnfestados()).thenReturn(quantidadeDeCortesEnfestados);
        when(enfestador.getAtivo()).thenReturn(ativo);

        return enfestador;
    }
}