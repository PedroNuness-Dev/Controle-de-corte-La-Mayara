package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.request.CortadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.exception.ResourceNotFoundException;
import com.PedroNunesDev.Controle_de_Corte.model.Cortador;
import com.PedroNunesDev.Controle_de_Corte.repository.CortadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CortadorService {

    private static final Logger logger = LoggerFactory.getLogger(CortadorService.class);
    private final CortadorRepository cortadorRepository;

    public CortadorService(CortadorRepository cortadorRepository) {
        this.cortadorRepository = cortadorRepository;
    }

    @Transactional(readOnly = true)
    public CortadorDtoResponse findById(Long id){

        logger.info("Buscando cortador com ID: {}", id);

        Cortador cortador = cortadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID " + id + " não encontrado"));

        logger.debug("Cortador encontrado: {}", cortador.getNome());

        return new CortadorDtoResponse(cortador.getId(), cortador.getNome(), cortador.getQuantidadeDeCortesCortados());
    }

    @Transactional(readOnly = true)
    public List<CortadorDtoResponse> buscarCortadores(){

        return cortadorRepository.findAll()
                .stream()
                .filter(cortador -> cortador.getAtivo() == true)
                .map(cortador -> {
                    return new CortadorDtoResponse(cortador.getId(), cortador.getNome(), cortador.getQuantidadeDeCortesCortados());
                })
                .toList();
    }

    @Transactional
    public CortadorDtoResponse cadastrarCortador(CortadorDtoRequest cortadorDtoRequest){

        logger.info("Criando novo cortador: {}", cortadorDtoRequest.nome());

        Cortador cortador = Cortador.builder()
                .nome(cortadorDtoRequest.nome())
                .ativo(true)
                .build();

        Cortador cortadorSalvo = cortadorRepository.save(cortador);

        logger.info("Cortador criado com sucesso. ID: {}", cortadorSalvo.getId());

        return new CortadorDtoResponse(cortadorSalvo.getId(),cortadorSalvo.getNome(),0);
    }

    @Transactional
    public CortadorDtoResponse update(Long id, CortadorDtoRequest cortadorDtoRequest){

        logger.info("Atualizando cortador com ID: {}", id);

        Cortador cortador = cortadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID " + id + " não encontrado"));

        cortador.setNome(cortadorDtoRequest.nome());

        Cortador cortadorAtualizado = cortadorRepository.save(cortador);

        logger.info("Cortador atualizado com sucesso. ID: {}", cortadorAtualizado.getId());

        return new CortadorDtoResponse(cortadorAtualizado.getId(), cortadorAtualizado.getNome(), cortadorAtualizado.getQuantidadeDeCortesCortados());
    }

    @Transactional
    public void desativarCortador(Long id){

        logger.info("Deletando cortador com ID: {}", id);

        Cortador cortadorBuscado = cortadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID: "+id+" não encontrado"));

        cortadorBuscado.setAtivo(false);

        cortadorRepository.save(cortadorBuscado);

        logger.info("Cortador desativado com sucesso. ID: {}", id);
    }
}
