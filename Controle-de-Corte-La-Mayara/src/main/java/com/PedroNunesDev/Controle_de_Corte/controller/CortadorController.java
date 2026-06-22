package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.request.CortadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.service.CortadorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar operações relacionadas a Cortadores.
 */
@RestController
@RequestMapping("/cortador")
@CrossOrigin("*")
public class CortadorController {

    private static final Logger logger = LoggerFactory.getLogger(CortadorController.class);
    private final CortadorService cortadorService;

    public CortadorController(CortadorService cortadorService) {
        this.cortadorService = cortadorService;
    }

    /**
     * Busca um cortador por ID.
     * @param id ID do cortador
     * @return Dados do cortador encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<CortadorDtoResponse> findById(@PathVariable Long id) {

        logger.debug("Requisição recebida: GET /cortador/{}", id);

        CortadorDtoResponse cortador = cortadorService.findById(id);

        return ResponseEntity.ok(cortador);
    }

    @GetMapping
    public ResponseEntity<List<CortadorDtoResponse>> buscarCortadores(){

        List<CortadorDtoResponse> cortadores = cortadorService.buscarCortadores();

        return ResponseEntity.ok(cortadores);
    }

    /**
     * Cria um novo cortador.
     * @param cortadorDtoRequest Dados do novo cortador
     * @return Cortador criado com status 201
     */
    @PostMapping
    public ResponseEntity<CortadorDtoResponse> cadastrarCortador(@RequestBody @Valid CortadorDtoRequest cortadorDtoRequest) {

        logger.debug("Requisição recebida: POST /cortador");

        CortadorDtoResponse cortadorNovo = cortadorService.cadastrarCortador(cortadorDtoRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(cortadorNovo);
    }

    /**
     * Atualiza um cortador existente.
     * @param id ID do cortador
     * @param cortadorDtoRequest Dados atualizados do cortador
     * @return Cortador atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<CortadorDtoResponse> update(@PathVariable Long id, @RequestBody @Valid CortadorDtoRequest cortadorDtoRequest) {

        logger.debug("Requisição recebida: PUT /cortador/{}", id);

        CortadorDtoResponse cortadorAtualizado = cortadorService.update(id, cortadorDtoRequest);

        return ResponseEntity.ok(cortadorAtualizado);
    }

    /**
     * Desativa um cortador por ID.
     * @param id ID do cortador
     * @return Status 204 No Content
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        logger.debug("Requisição recebida: DELETE /cortador/{}", id);

        cortadorService.desativarCortador(id);

        return ResponseEntity.noContent().build();
    }
}