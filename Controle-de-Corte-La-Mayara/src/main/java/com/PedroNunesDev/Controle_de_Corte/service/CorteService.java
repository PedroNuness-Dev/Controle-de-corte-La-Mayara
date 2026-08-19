package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.request.CorteDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.request.CorteUpdateDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CorteDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.enums.CorteStatus;
import com.PedroNunesDev.Controle_de_Corte.exception.ResourceNotFoundException;
import com.PedroNunesDev.Controle_de_Corte.mapper.CorteMapper;
import com.PedroNunesDev.Controle_de_Corte.model.Cortador;
import com.PedroNunesDev.Controle_de_Corte.model.Corte;
import com.PedroNunesDev.Controle_de_Corte.model.Enfestador;
import com.PedroNunesDev.Controle_de_Corte.model.Lote;
import com.PedroNunesDev.Controle_de_Corte.repository.CortadorRepository;
import com.PedroNunesDev.Controle_de_Corte.repository.CorteRepository;
import com.PedroNunesDev.Controle_de_Corte.repository.EnfestadorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class CorteService {

    private final CortadorRepository cortadorRepository;
    private final EnfestadorRepository enfestadorRepository;
    private final LoteService loteService;
    private final CorteRepository corteRepository;
    private final CorteMapper corteMapper;

    public CorteService(CortadorRepository cortadorRepository, EnfestadorRepository enfestadorRepository, LoteService loteService, CorteRepository corteRepository, CorteMapper corteMapper) {
        this.cortadorRepository = cortadorRepository;
        this.enfestadorRepository = enfestadorRepository;
        this.loteService = loteService;
        this.corteRepository = corteRepository;
        this.corteMapper = corteMapper;
    }

    @Transactional(readOnly = true)
    public CorteDtoResponse findById(Long id){

        Corte corteBuscado = corteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corte com ID: "+id+" encontrado"));

        return corteMapper.toDto(corteBuscado);
    }

    @Cacheable("cortes")
    @Transactional(readOnly = true)
    public List<CorteDtoResponse> buscarPorMes(Integer mes, Integer ano){

        Integer mesParaBuscar = verificarMes(mes);

        Integer anoParaBuscar = verificarAno(ano);

        LocalDate diaPrimeiro = LocalDate.of(anoParaBuscar, mesParaBuscar, 1);
        LocalDate diaUltimo = diaPrimeiro.withDayOfMonth(diaPrimeiro.lengthOfMonth());

        List<Corte> cortesBuscados = corteRepository.buscarPorMes(diaPrimeiro, diaUltimo);

        return cortesBuscados.stream()
                .map(corteMapper::toDto)
                .toList();
    }

    @Cacheable("cortes")
    @Transactional(readOnly = true)
    public List<CorteDtoResponse> buscarPorStatus(Integer mes, Integer ano, String status){

        //uso o método de cima para buscar os cortes do mes, aproveitando a logica
        List<CorteDtoResponse> cortesBuscados = buscarPorMes(mes, ano);

        if (cortesBuscados.isEmpty()) return List.of();

        //Filtro os cortes pelo status passado no método, assim retornando apenas o pedido
        return cortesBuscados.stream()
                .filter(corte -> {
                    CorteStatus corteStatus = CorteStatus.from(status);

                    return corte.corteStatus().equals(corteStatus);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CorteDtoResponse> buscarCortesPorNomeOuLote(String stringParaBuscar, Integer mes, Integer ano){

        Integer mesParaBuscar = verificarMes(mes);

        Integer anoParaBuscar = verificarAno(ano);

        LocalDate diaPrimeiro = LocalDate.of(anoParaBuscar, mesParaBuscar, 1);
        LocalDate diaUltimo = diaPrimeiro.withDayOfMonth(diaPrimeiro.lengthOfMonth());

        List<Corte> cortesBuscados = corteRepository.buscarPorNomeOuLote(stringParaBuscar, diaPrimeiro, diaUltimo);

        return cortesBuscados
                .stream()
                .map(corteMapper::toDto)
                .toList();
    }

    private Integer verificarMes(Integer mes){

        Integer mesParaBuscar = (mes == null || mes == 0) ? LocalDate.now().getMonthValue() : mes;

        if (mesParaBuscar < 1 || mesParaBuscar > 12) {
            throw new IllegalArgumentException("Mês inválido. O valor deve estar entre 1 e 12.");
        }

        return mesParaBuscar;
    }

    private Integer verificarAno(Integer ano){

        Integer anoParaBuscar = (ano == null || ano == 0) ? LocalDate.now().getYear() : ano;

        return anoParaBuscar;
    }

    @CacheEvict(value = "cortes", allEntries = true)
    @Transactional
    public CorteDtoResponse criarCorte(CorteDtoRequest corteDtoRequest){

        Lote lote = loteService.buscarLoteModel();

        Corte corte = Corte.builder()
                .dataDeRegistro(corteDtoRequest.dataDeRegistro())
                .corteStatus(CorteStatus.PENDENTE)
                .loteFormatado(lote.formatarLote())
                .lote(lote)
                .nomeModelo(corteDtoRequest.nomeModelo())
                .quantidadeTotal(corteDtoRequest.quantidadeTotal())
                .observacao(corteDtoRequest.observacao())
                .build();

        Corte corteSalvo = corteRepository.save(corte);

        return corteMapper.toDto(corteSalvo);
    }

    @CacheEvict(value = "cortes", allEntries = true)
    @Transactional
    public CorteDtoResponse atualizarCorte(Long idCorte, CorteUpdateDtoRequest corteUpdateDtoRequest){

        Enfestador enfestador = null;
        Cortador cortador = null;

        Corte corteParaAtualizar = corteRepository.findById(idCorte)
                .orElseThrow(() -> new ResourceNotFoundException("Corte com ID: "+idCorte+" encontrado"));

        if (corteUpdateDtoRequest.idEnfestador() != null){
            enfestador = enfestadorRepository.findById(corteUpdateDtoRequest.idEnfestador())
                    .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID: "+corteUpdateDtoRequest.idEnfestador()+" encontrado"));
        }

        if (corteUpdateDtoRequest.idCortador() != null){
            cortador = cortadorRepository.findById(corteUpdateDtoRequest.idCortador())
                    .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID: "+corteUpdateDtoRequest.idCortador()+" encontrado"));
        }

        corteParaAtualizar = atualizarDadosDoCorte(corteParaAtualizar, corteUpdateDtoRequest,enfestador,cortador);
        Corte corteAtualizado = corteRepository.save(corteParaAtualizar);

        return corteMapper.toDto(corteAtualizado);
    }

    private Corte atualizarDadosDoCorte(Corte corte, CorteUpdateDtoRequest corteUpdateDtoRequest, Enfestador enfestador, Cortador cortador){

        corte.setNomeModelo(corteUpdateDtoRequest.nomeModelo());
        corte.setDataDeCorte(corteUpdateDtoRequest.dataDeCorte());
        corte.setLoteFormatado(corteUpdateDtoRequest.loteFormatado());
        corte.setQuantidadeTotal(corteUpdateDtoRequest.quantidadeTotal());
        corte.setObservacao(corteUpdateDtoRequest.observacao());
        corte.setEnfestador(enfestador);
        corte.setCortador(cortador);

        if (corte.getCortador() != null && corte.getEnfestador() != null){
            corte.setCorteStatus(CorteStatus.CORTADO);
        } else if (corte.getEnfestador() != null){
            corte.setCorteStatus(CorteStatus.ENFESTADO);
            corte.setDataDeCorte(null);
        }
        else {
            corte.setCorteStatus(CorteStatus.PENDENTE);
            corte.setDataDeCorte(null);
        }

        return corte;
    }

    @CacheEvict(value = "cortes", allEntries = true)
    @Transactional
    public CorteDtoResponse atualizarEnfestadorDoCorte(Long idCorte, Long idEnfestador){

        Assert.notNull(idCorte, "Id do corte é obirgatório para a atualização");
        Assert.notNull(idEnfestador, "Id do enfestador é orbigatório para a atualização");

        Corte corteBuscado = corteRepository.findById(idCorte)
                .orElseThrow(() -> new ResourceNotFoundException("Corte com ID: "+idCorte+" encontrado"));

        Enfestador enfestadorBuscado = enfestadorRepository.findById(idEnfestador)
                .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID: "+idEnfestador+" encontrado"));

        corteBuscado.setEnfestador(enfestadorBuscado);
        corteBuscado.setCorteStatus(CorteStatus.ENFESTADO);

        Corte corteAtualizado = corteRepository.save(corteBuscado);

        return corteMapper.toDto(corteAtualizado);
    }

    @CacheEvict(value = "cortes", allEntries = true)
    @Transactional
    public CorteDtoResponse atualizarCortadorDoCorte(Long idCorte, Long idCortador){

        Assert.notNull(idCorte, "Id do corte é obirgatório para a atualização");
        Assert.notNull(idCortador, "Id do cortador é obrigatório para a atualização");

        Corte corteBuscado = corteRepository.findById(idCorte)
                .orElseThrow(() -> new ResourceNotFoundException("Corte com ID: "+idCorte+" encontrado"));

        Cortador cortadorBuscado = cortadorRepository.findById(idCortador)
                .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID: "+idCortador+" encontrado"));

        corteBuscado.setCortador(cortadorBuscado);
        corteBuscado.setCorteStatus(CorteStatus.CORTADO);

        Corte corteAtualizado = corteRepository.save(corteBuscado);

        return corteMapper.toDto(corteAtualizado);
    }

    @CacheEvict(value = "cortes", allEntries = true)
    @Transactional
    public void cancelarCorte(Long idCorte){

        Corte corteBuscado = corteRepository.findById(idCorte)
                .orElseThrow(() -> new ResourceNotFoundException("Corte com ID: "+idCorte+" encontrado"));

        corteBuscado.setCorteStatus(CorteStatus.CANCELADO);
    }

    @Transactional(readOnly = true)
    public int relatorioDoMesDoCorte(){

        List<CorteDtoResponse> cortesDoMes = buscarPorMes(null, null);

        int cortesRegistrados = cortesDoMes.size();

        return cortesRegistrados;
    }

    @CacheEvict(value = "cortes", allEntries = true)
    @Transactional
    public void excluirCorte(Long id){

       if (!corteRepository.existsById(id)){
           throw new ResourceNotFoundException("Corte com ID: "+id+ " não encontrado");
       }

       corteRepository.deleteById(id);
    }
}
