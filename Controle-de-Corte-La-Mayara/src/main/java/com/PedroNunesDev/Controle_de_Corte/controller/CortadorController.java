package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.request.CortadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview;
import com.PedroNunesDev.Controle_de_Corte.service.CortadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CortadorController {

    private final CortadorService cortadorService;

    /**
     * Busca um cortador por ID.
     * @param id ID do cortador
     * @return Dados do cortador encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<CortadorDtoResponse> buscarCortadorPorId(@PathVariable Long id) {

        CortadorDtoResponse cortador = cortadorService.buscarCortadorPorId(id);

        return ResponseEntity.ok(cortador);
    }

    @GetMapping
    public ResponseEntity<List<CortadorDtoResponse>> buscarCortadoresAtivos(){

        List<CortadorDtoResponse> cortadores = cortadorService.buscarCortadoresAtivos();

        return ResponseEntity.ok(cortadores);
    }

    @GetMapping("/overview")
    public ResponseEntity<List<CortadorOverview>> buscarDetalhesDosCortadoresAtivos(){

        List<CortadorOverview> responses = cortadorService.buscarDetalhesDosCortadoresAtivos();

        return ResponseEntity.ok(responses);
    }

    /**
     * Cria um novo cortador.
     * @param cortadorDtoRequest Dados do novo cortador
     * @return Cortador criado com status 201
     */
    @PostMapping
    public ResponseEntity<CortadorDtoResponse> cadastrarCortador(@RequestBody @Valid CortadorDtoRequest cortadorDtoRequest) {

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
    public ResponseEntity<CortadorOverview> atualizarCortador(@PathVariable Long id, @RequestBody @Valid CortadorDtoRequest cortadorDtoRequest) {

        CortadorOverview cortadorAtualizado = cortadorService.atualizarCortador(id, cortadorDtoRequest);

        return ResponseEntity.ok(cortadorAtualizado);
    }

    /**
     * Desativa um cortador por ID.
     * @param id ID do cortador
     * @return Detalhes do cortador desativado
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CortadorOverview> desativarCortadorPorId(@PathVariable Long id) {

        CortadorOverview response = cortadorService.desativarCortador(id);

        return ResponseEntity.ok(response);
    }
}