package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.request.EnfestadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorOverview;
import com.PedroNunesDev.Controle_de_Corte.exception.ResourceNotFoundException;
import com.PedroNunesDev.Controle_de_Corte.model.Enfestador;
import com.PedroNunesDev.Controle_de_Corte.repository.EnfestadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnfestadorService {

    private final EnfestadorRepository enfestadorRepository;

    @Transactional(readOnly = true)
    public EnfestadorDtoResponse buscarEnfestadorPorId(Long id){

        log.info("Buscando enfestador com ID: {}", id);

        Enfestador enfestador = enfestadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID " + id + " não encontrado"));

        log.debug("Enfestador encontrado: {}", enfestador.getNome());

        return new EnfestadorDtoResponse(enfestador.getId(), enfestador.getNome());
    }

    @Transactional(readOnly = true)
    public List<EnfestadorDtoResponse> buscarEnfestadoresAtivos(){

        log.info("Iniciando busca de todos os enfestadores ativos cadastrados no sistema");

        List<EnfestadorDtoResponse> enfestadores = enfestadorRepository.buscarEnfestadoresAtivos()
                .stream()
                .map(enfestador -> new EnfestadorDtoResponse(
                        enfestador.getId(),
                        enfestador.getNome()
                ))
                .toList();

        log.debug("Total de enfestadores ativos encontrados: {}", enfestadores.size());

        return enfestadores;
    }

    @Transactional(readOnly = true)
    public List<EnfestadorOverview> buscarDetalhesDosEnfestadoresAtivos(){

        log.info("Iniciando busca dos detalhes de todos os enfestadores ativos cadastrados no sistema");

        List<EnfestadorOverview> enfestadores = enfestadorRepository.buscarEnfestadoresESuasQuantidadesDeCortes();

        log.debug("Total de enfestadores com overview encontrados: {}", enfestadores.size());

        return enfestadores;
    }

    @Transactional
    public EnfestadorDtoResponse cadastrarEnfestador(EnfestadorDtoRequest enfestadorDtoRequest){

        log.info("Criando novo enfestador: {}", enfestadorDtoRequest.nome());

        Enfestador enfestador = Enfestador.builder()
                .nome(enfestadorDtoRequest.nome())
                .ativo(true)
                .build();

        Enfestador enfestadorSalvo = enfestadorRepository.save(enfestador);

        log.info("Enfestador criado com sucesso. ID: {}", enfestadorSalvo.getId());

        return new EnfestadorDtoResponse(enfestadorSalvo.getId(), enfestadorSalvo.getNome());
    }

    @Transactional
    public EnfestadorOverview atualizarEnfestador(Long id, EnfestadorDtoRequest enfestadorDtoRequest){

        log.info("Atualizando enfestador com ID: {}", id);

        Enfestador enfestador = enfestadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID " + id + " não encontrado"));

        enfestador.setNome(enfestadorDtoRequest.nome());

        Enfestador enfestadorAtualizado = enfestadorRepository.save(enfestador);

        log.info("Enfestador atualizado com sucesso. ID: {}", enfestadorAtualizado.getId());

        return toOverview(enfestadorAtualizado);
    }

    @Transactional
    public EnfestadorOverview desativarEnfestador(Long id){

        log.info("Desativando enfestador com ID: {}", id);

        Enfestador enfestadorBuscado = enfestadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID: " + id + " não encontrado"));

        enfestadorBuscado.setAtivo(false);

        enfestadorRepository.save(enfestadorBuscado);

        log.info("Enfestador desativado com sucesso. ID: {}", id);

        return toOverview(enfestadorBuscado);
    }

    private EnfestadorOverview toOverview(Enfestador enfestador){
        return new EnfestadorOverview(
                enfestador.getId(),
                enfestador.getNome(),
                (long) enfestador.getQuantidadeDeCortesEnfestados(),
                enfestador.getAtivo()
        );
    }
}