package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.request.CortadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview;
import com.PedroNunesDev.Controle_de_Corte.exception.ResourceNotFoundException;
import com.PedroNunesDev.Controle_de_Corte.model.Cortador;
import com.PedroNunesDev.Controle_de_Corte.repository.CortadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CortadorService {

    private final CortadorRepository cortadorRepository;

    @Transactional(readOnly = true)
    public CortadorDtoResponse buscarCortadorPorId(Long id){

        log.info("Buscando cortador com ID: {}", id);

        Cortador cortador = cortadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID " + id + " não encontrado"));

        log.debug("Cortador encontrado: {}", cortador.getNome());

        return new CortadorDtoResponse(cortador.getId(), cortador.getNome());
    }

    @Transactional(readOnly = true)
    public List<CortadorDtoResponse> buscarCortadoresAtivos(){

        log.info("iniciando busca de todos os cortadores ativos cadastrados no sistema");

        return cortadorRepository.buscarCortadoresAtivos()
                .stream()
                .map(cortador -> new CortadorDtoResponse(cortador.getId(), cortador.getNome()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CortadorOverview> buscarDetalhesDosCortadoresAtivos(){

        log.info("iniciando busca de todos os detalhes dos cortadores ativos cadastrados no sistema");

        return cortadorRepository.buscarCortadoresESuasQuantidadesDeCortes();
    }

    @Transactional
    public CortadorDtoResponse cadastrarCortador(CortadorDtoRequest cortadorDtoRequest){

        log.info("Criando novo cortador: {}", cortadorDtoRequest.nome());

        Cortador cortador = Cortador.builder()
                .nome(cortadorDtoRequest.nome())
                .ativo(true)
                .build();

        Cortador cortadorSalvo = cortadorRepository.save(cortador);

        log.info("Cortador criado com sucesso. ID: {}", cortadorSalvo.getId());

        return new CortadorDtoResponse(cortadorSalvo.getId(),cortadorSalvo.getNome());
    }

    @Transactional
    public CortadorOverview atualizarCortador(Long id, CortadorDtoRequest cortadorDtoRequest){

        log.info("Atualizando cortador com ID: {}", id);

        Cortador cortador = cortadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID " + id + " não encontrado"));

        cortador.setNome(cortadorDtoRequest.nome());

        Cortador cortadorAtualizado = cortadorRepository.save(cortador);

        log.info("Cortador atualizado com sucesso. ID: {}", cortadorAtualizado.getId());

        return toOverview(cortadorAtualizado);
    }

    @Transactional
    public CortadorOverview desativarCortador(Long id){

        log.info("Desativando cortador com ID: {}", id);

        Cortador cortadorBuscado = cortadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID: "+id+" não encontrado"));

        cortadorBuscado.setAtivo(false);

        cortadorRepository.save(cortadorBuscado);

        log.info("Cortador desativado com sucesso. ID: {}", id);

        return toOverview(cortadorBuscado);
    }

    private CortadorOverview toOverview(Cortador cortador){

        return new CortadorOverview(
                cortador.getId(),
                cortador.getNome(),
                (long) cortador.getQuantidadeDeCortes(),
                cortador.getAtivo()
        );
    }
}
