package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.request.EnfestadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.service.EnfestadorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar operações relacionadas a Enfestadores.
 */
@RestController
@RequestMapping("/enfestador")
@CrossOrigin("*")
public class EnfestadorController {

    private static final Logger logger = LoggerFactory.getLogger(EnfestadorController.class);
    private final EnfestadorService enfestadorService;

    public EnfestadorController(EnfestadorService enfestadorService) {
        this.enfestadorService = enfestadorService;
    }

    /**
     * Busca um enfestador por ID.
     * @param id ID do enfestador
     * @return Dados do enfestador encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnfestadorDtoResponse> findById(@PathVariable Long id) {

        logger.debug("Requisição recebida: GET /enfestador/{}", id);

        EnfestadorDtoResponse enfestador = enfestadorService.findById(id);

        return ResponseEntity.ok(enfestador);
    }

    @GetMapping
    public ResponseEntity<List<EnfestadorDtoResponse>> buscarEnfestadores(){

        List<EnfestadorDtoResponse> enfestadores = enfestadorService.buscarEnfestadores();

        return ResponseEntity.ok(enfestadores);
    }

    /**
     * Cria um novo enfestador.
     * @param enfestadorDtoRequest Dados do novo enfestador
     * @return Enfestador criado com status 201
     */
    @PostMapping
    public ResponseEntity<EnfestadorDtoResponse> cadastrarEnfestador(@RequestBody @Valid EnfestadorDtoRequest enfestadorDtoRequest) {

        logger.debug("Requisição recebida: POST /enfestador");

        EnfestadorDtoResponse enfestadorNovo = enfestadorService.cadastrarEnfestador(enfestadorDtoRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(enfestadorNovo);
    }

    /**
     * Atualiza um enfestador existente.
     * @param id ID do enfestador
     * @param enfestadorDtoRequest Dados atualizados do enfestador
     * @return Enfestador atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<EnfestadorDtoResponse> update(@PathVariable Long id, @RequestBody @Valid EnfestadorDtoRequest enfestadorDtoRequest) {

        logger.debug("Requisição recebida: PUT /enfestador/{}", id);

        EnfestadorDtoResponse enfestadorAtualizado = enfestadorService.update(id, enfestadorDtoRequest);

        return ResponseEntity.ok(enfestadorAtualizado);
    }

    /**
     * Desativa um enfestador por ID.
     * @param id ID do enfestador
     * @return Status 204 No Content
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        logger.debug("Requisição recebida: DELETE /enfestador/{}", id);

        enfestadorService.desativarEnfestador(id);

        return ResponseEntity.noContent().build();
    }
}
