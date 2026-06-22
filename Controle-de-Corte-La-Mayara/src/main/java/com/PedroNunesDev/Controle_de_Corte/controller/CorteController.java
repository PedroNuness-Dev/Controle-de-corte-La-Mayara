package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.request.CorteDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.request.CorteUpdateDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CorteDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EstatisticaPessoaDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.service.CorteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar operações relacionadas a Cortes.
 */
@RestController
@RequestMapping("/corte")
@CrossOrigin("*")
public class CorteController {

    private static final Logger logger = LoggerFactory.getLogger(CorteController.class);
    private final CorteService corteService;

    public CorteController(CorteService corteService) {
        this.corteService = corteService;
    }

    /**
     * Busca um corte por ID.
     * @param id ID do corte
     * @return Dados do corte encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<CorteDtoResponse> findById(@PathVariable Long id) {

        logger.debug("Requisição recebida: GET /corte/{}", id);

        CorteDtoResponse corte = corteService.findById(id);

        return ResponseEntity.ok(corte);
    }

    /**
     * Busca cortes do mês atual.
     * @return Lista de cortes do mês
     */
    @GetMapping("/buscar/mes")
    public ResponseEntity<List<CorteDtoResponse>> buscarPorMes(@RequestParam(required = false) Integer mes, @RequestParam(required = false) Integer ano) {

        logger.debug("Requisição recebida: GET /corte/mes");

        List<CorteDtoResponse> cortes = corteService.buscarPorMes(mes, ano);

        return ResponseEntity.ok(cortes);
    }

    /**
     * Busca cortes do mês atual.
     * @return Lista de cortes do mês
     */
    @GetMapping("/buscar/mes/status")
    public ResponseEntity<List<CorteDtoResponse>> buscarPorMesEhStatus(@RequestParam(required = false) Integer mes, @RequestParam(required = false) Integer ano, @RequestParam String status) {

        logger.debug("Requisição recebida: GET /corte/mes/status");

        List<CorteDtoResponse> cortes = corteService.buscarPorStatus(mes, ano, status);

        return ResponseEntity.ok(cortes);
    }

    /**
     * Busca cortes do mês atual e pelo nome ou lote.
     * @return Lista de cortes do mês
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<CorteDtoResponse>> buscarCortesPorNomeOuLote(@RequestParam String tipo, @RequestParam(required = false) Integer mes, @RequestParam(required = false) Integer ano){

        List<CorteDtoResponse> cortes = corteService.buscarCortesPorNomeOuLote(tipo, mes, ano);

        return ResponseEntity.ok(cortes);
    }

    /**
     * Gera um relatório do total de cortes realizados no mês atual.
     * @return Quantidade de cortes do mês
     */
    @GetMapping("/relatorio")
    public ResponseEntity<Integer> gerarRelatorio(){

        logger.debug("Requisição recebida: GET /corte/relatorio");

        Integer totalCortes = corteService.relatorioDoMesDoCorte();

        return ResponseEntity.ok(totalCortes);
    }

    /**
     * Cria um novo corte.
     * @param corteDtoRequest Dados do novo corte
     * @return Corte criado com status 201
     */
    @PostMapping
    public ResponseEntity<CorteDtoResponse> criarCorte(@RequestBody @Valid CorteDtoRequest corteDtoRequest) {

        logger.debug("Requisição recebida: POST /corte");

        CorteDtoResponse corteNovo = corteService.criarCorte(corteDtoRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(corteNovo);
    }

    /**[
     *
     * @param id
     * @param corteUpdateDtoRequest
     * @return
     */
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<CorteDtoResponse> atualzarCorte(@PathVariable Long id, @RequestBody @Valid CorteUpdateDtoRequest corteUpdateDtoRequest) {

        logger.debug("Requisição recebida: PUT /corte/atualizar?id={}", id);

        CorteDtoResponse corteAtualizado = corteService.atualizarCorte(id, corteUpdateDtoRequest);

        return ResponseEntity.ok(corteAtualizado);
    }

    /**
     * Atualiza o enfestador de um corte.
     * @param idCorte ID do corte
     * @param idEnfestador ID do enfestador
     * @return Corte atualizado
     */
    @PutMapping("/{idCorte}/enfestador/{idEnfestador}")
    public ResponseEntity<CorteDtoResponse> atualizarEnfestador(@PathVariable Long idCorte, @PathVariable Long idEnfestador) {

        logger.debug("Requisição recebida: PUT /corte/{}/enfestador/{}", idCorte, idEnfestador);

        CorteDtoResponse corteAtualizado = corteService.atualizarEnfestador(idCorte, idEnfestador);

        return ResponseEntity.ok(corteAtualizado);
    }

    /**
     * Atualiza o cortador de um corte.
     * @param idCorte ID do corte
     * @param idCortador ID do cortador
     * @return Corte atualizado
     */
    @PutMapping("/{idCorte}/cortador/{idCortador}")
    public ResponseEntity<CorteDtoResponse> atualizarCortador(@PathVariable Long idCorte, @PathVariable Long idCortador) {

        logger.debug("Requisição recebida: PUT /corte/{}/cortador/{}", idCorte, idCortador);

        CorteDtoResponse corteAtualizado = corteService.atualizarCortador(idCorte, idCortador);

        return ResponseEntity.ok(corteAtualizado);
    }

    /**
     * Cancela um corte.
     * @param idCorte ID do corte
     * @return Status 200 OK
     */
    @PutMapping("/{idCorte}/cancelar")
    public ResponseEntity<Void> cancelarCorte(@PathVariable Long idCorte) {

        logger.debug("Requisição recebida: PUT /corte/{}/cancelar", idCorte);

        corteService.cancelarCorte(idCorte);

        return ResponseEntity.ok().build();
    }
    /**
     * Busca estatisticas de uma pessoa pelo seu nome.
     * @param nome da Pessoa
     * @return Status 200 OK
     */

    @GetMapping("/estatistica")
    public ResponseEntity<EstatisticaPessoaDtoResponse> buscarEstatistica(@RequestParam String nome){

        EstatisticaPessoaDtoResponse estatistica = corteService.buscar(nome);

        return ResponseEntity.ok(estatistica);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCorte(@PathVariable Long id){

        corteService.excluirCorte(id);

        return ResponseEntity.noContent().build();
    }
}
