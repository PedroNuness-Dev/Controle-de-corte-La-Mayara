package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.request.EnfestadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.exception.ResourceNotFoundException;
import com.PedroNunesDev.Controle_de_Corte.model.Enfestador;
import com.PedroNunesDev.Controle_de_Corte.repository.EnfestadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class EnfestadorService {

    private static final Logger logger = LoggerFactory.getLogger(EnfestadorService.class);
    private final EnfestadorRepository enfestadorRepository;

    public EnfestadorService(EnfestadorRepository enfestadorRepository) {
        this.enfestadorRepository = enfestadorRepository;
    }

    @Transactional(readOnly = true)
    public EnfestadorDtoResponse findById(Long id){

        logger.info("Buscando enfestador com ID: {}", id);

        Enfestador enfestador = enfestadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID " + id + " não encontrado"));

        logger.debug("Enfestador encontrado: {}", enfestador.getNome());

        return new EnfestadorDtoResponse(enfestador.getId(), enfestador.getNome(), enfestador.getQuantidadeDeCortesEnfestados());
    }

    @Transactional(readOnly = true)
    public List<EnfestadorDtoResponse> buscarEnfestadores(){

        return enfestadorRepository.findAll()
                .stream()
                .filter(enfestador -> enfestador.getAtivo() == true)
                .map(enfestador -> {
                    return new EnfestadorDtoResponse(enfestador.getId(), enfestador.getNome(),enfestador.getQuantidadeDeCortesEnfestados());
                })
                .sorted(Comparator.comparing(EnfestadorDtoResponse::nome))
                .toList();
    }

    @Transactional
    public EnfestadorDtoResponse cadastrarEnfestador(EnfestadorDtoRequest enfestadorDtoRequest){

        logger.info("Criando novo enfestador: {}", enfestadorDtoRequest.nome());

        Enfestador enfestador = Enfestador.builder()
                .nome(enfestadorDtoRequest.nome())
                .ativo(true)
                .build();

        Enfestador enfestadorSalvo = enfestadorRepository.save(enfestador);

        logger.info("Enfestador criado com sucesso. ID: {}", enfestadorSalvo.getId());

        return new EnfestadorDtoResponse(enfestadorSalvo.getId(), enfestadorSalvo.getNome(),0);
    }

    @Transactional
    public EnfestadorDtoResponse update(Long id, EnfestadorDtoRequest enfestadorDtoRequest){

        logger.info("Atualizando enfestador com ID: {}", id);

        Enfestador enfestador = enfestadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID " + id + " não encontrado"));

        enfestador.setNome(enfestadorDtoRequest.nome());

        Enfestador enfestadorAtualizado = enfestadorRepository.save(enfestador);

        logger.info("Enfestador atualizado com sucesso. ID: {}", enfestadorAtualizado.getId());

        return new EnfestadorDtoResponse(enfestadorAtualizado.getId(), enfestadorAtualizado.getNome(), enfestadorAtualizado.getQuantidadeDeCortesEnfestados());
    }

    @Transactional
    public void desativarEnfestador(Long id){

        logger.info("Deletando enfestador com ID: {}", id);

        Enfestador enfestadorBuscado = enfestadorRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID: "+id+" não encontrado"));

        enfestadorBuscado.setAtivo(false);

        enfestadorRepository.save(enfestadorBuscado);

        logger.info("Enfestador desativado com sucesso. ID: {}", id);
    }
}
