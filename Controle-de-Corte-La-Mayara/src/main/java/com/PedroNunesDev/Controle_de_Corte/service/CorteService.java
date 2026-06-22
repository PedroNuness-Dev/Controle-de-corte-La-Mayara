package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.request.CorteDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.request.CorteUpdateDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CorteDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EstatisticaPessoaDtoResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

    @Transactional
    public CorteDtoResponse atualizarCorte(Long idCorte, CorteUpdateDtoRequest corteUpdateDtoRequest){

        Enfestador enfestador = null;
        Cortador cortador = null;

        if (corteUpdateDtoRequest.idEnfestador() != null){
            enfestador = enfestadorRepository.findById(corteUpdateDtoRequest.idEnfestador())
                    .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID: "+corteUpdateDtoRequest.idEnfestador()+" encontrado"));
        }

        if (corteUpdateDtoRequest.idCortador() != null){
            cortador = cortadorRepository.findById(corteUpdateDtoRequest.idCortador())
                    .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID: "+corteUpdateDtoRequest.idCortador()+" encontrado"));
        }

        Corte corteParaAtualizar = corteRepository.findById(idCorte)
                .orElseThrow(() -> new ResourceNotFoundException("Corte com ID: "+idCorte+" encontrado"));

        corteParaAtualizar.setNomeModelo(corteUpdateDtoRequest.nomeModelo());
        corteParaAtualizar.setDataDeCorte(corteUpdateDtoRequest.dataDeCorte());
        corteParaAtualizar.setLoteFormatado(corteUpdateDtoRequest.loteFormatado());
        corteParaAtualizar.setQuantidadeTotal(corteUpdateDtoRequest.quantidadeTotal());
        corteParaAtualizar.setObservacao(corteUpdateDtoRequest.observacao());
        corteParaAtualizar.setEnfestador(enfestador);
        corteParaAtualizar.setCortador(cortador);

        if (corteParaAtualizar.getCortador() != null && corteParaAtualizar.getEnfestador() != null){
            corteParaAtualizar.setDataDeCorte(LocalDate.now());
            corteParaAtualizar.setCorteStatus(CorteStatus.CORTADO);
        } else if (corteParaAtualizar.getEnfestador() != null){
            corteParaAtualizar.setCorteStatus(CorteStatus.ENFESTADO);
            corteParaAtualizar.setDataDeCorte(null);
        }
        else {
            corteParaAtualizar.setCorteStatus(CorteStatus.PENDENTE);
            corteParaAtualizar.setDataDeCorte(null);
        }

        Corte corteAtualizado = corteRepository.save(corteParaAtualizar);

        return corteMapper.toDto(corteAtualizado);
    }

    @Transactional
    public CorteDtoResponse atualizarEnfestador(Long idCorte, Long idEnfestador){

        Corte corteBuscado = corteRepository.findById(idCorte)
                .orElseThrow(() -> new ResourceNotFoundException("Corte com ID: "+idCorte+" encontrado"));

        Enfestador enfestadorBuscado = enfestadorRepository.findById(idEnfestador)
                .orElseThrow(() -> new ResourceNotFoundException("Enfestador com ID: "+idEnfestador+" encontrado"));

        corteBuscado.setEnfestador(enfestadorBuscado);
        corteBuscado.setCorteStatus(CorteStatus.ENFESTADO);

        Corte corteAtualizado = corteRepository.save(corteBuscado);

        return corteMapper.toDto(corteAtualizado);
    }

    @Transactional
    public CorteDtoResponse atualizarCortador(Long idCorte, Long idCortador){

        Corte corteBuscado = corteRepository.findById(idCorte)
                .orElseThrow(() -> new ResourceNotFoundException("Corte com ID: "+idCorte+" encontrado"));

        Cortador cortadorBuscado = cortadorRepository.findById(idCortador)
                .orElseThrow(() -> new ResourceNotFoundException("Cortador com ID: "+idCortador+" encontrado"));

        corteBuscado.setCortador(cortadorBuscado);
        corteBuscado.setCorteStatus(CorteStatus.CORTADO);

        Corte corteAtualizado = corteRepository.save(corteBuscado);

        return corteMapper.toDto(corteAtualizado);
    }

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

    @Transactional(readOnly = true)
    public EstatisticaPessoaDtoResponse buscar(String nome){

        Long cortesEnfestados = corteRepository.buscarQuantidadeDeCortesPorEnfestador(nome);
        Long cortesCortados = corteRepository.buscarQuantidadeDeCortesPorCortador(nome);

        EstatisticaPessoaDtoResponse estatistica = new EstatisticaPessoaDtoResponse(nome, cortesEnfestados,cortesCortados);

        return estatistica;
    }

    @Transactional
    public void excluirCorte(Long id){

       if (!corteRepository.existsById(id)){
           throw new ResourceNotFoundException("Corte com ID: "+id+ " não encontrado");
       }

       corteRepository.deleteById(id);
    }
}
