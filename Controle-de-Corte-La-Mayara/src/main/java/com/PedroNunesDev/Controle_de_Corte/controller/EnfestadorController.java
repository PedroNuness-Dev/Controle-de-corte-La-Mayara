package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.request.EnfestadorDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorOverview;
import com.PedroNunesDev.Controle_de_Corte.service.EnfestadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class EnfestadorController {

    private final EnfestadorService enfestadorService;

    /**
     * Busca um enfestador por ID.
     * @param id ID do enfestador
     * @return Dados do enfestador encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnfestadorDtoResponse> buscarEnfestadorPorId(@PathVariable Long id) {

        EnfestadorDtoResponse enfestador = enfestadorService.buscarEnfestadorPorId(id);

        return ResponseEntity.ok(enfestador);
    }

    /**
     * Busca enfestadores ativos
     * @return Lista dos cortadores ativos
     */
    @GetMapping
    public ResponseEntity<List<EnfestadorDtoResponse>> buscarEnfestadoresAtivos(){

        List<EnfestadorDtoResponse> enfestadores = enfestadorService.buscarEnfestadoresAtivos();

        return ResponseEntity.ok(enfestadores);
    }

    /**
     * Busca detalhes dos enfestadores ativos
     * @return Lista dos detalhes dos cortadores ativos
     */
    @GetMapping("/overview")
    public ResponseEntity<List<EnfestadorOverview>> buscarDetalhesDosEnfestadoresAtivos(){

        List<EnfestadorOverview> responses = enfestadorService.buscarDetalhesDosEnfestadoresAtivos();

        return ResponseEntity.ok(responses);
    }

    /**
     * Cria um novo enfestador.
     * @param enfestadorDtoRequest Dados do novo enfestador
     * @return Enfestador criado com status 201
     */
    @PostMapping
    public ResponseEntity<EnfestadorDtoResponse> cadastrarEnfestador(@RequestBody @Valid EnfestadorDtoRequest enfestadorDtoRequest) {

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
    public ResponseEntity<EnfestadorOverview> atualizarEnfestador(@PathVariable Long id, @RequestBody @Valid EnfestadorDtoRequest enfestadorDtoRequest) {

        EnfestadorOverview enfestadorAtualizado = enfestadorService.atualizarEnfestador(id, enfestadorDtoRequest);

        return ResponseEntity.ok(enfestadorAtualizado);
    }

    /**
     * Desativa um enfestador por ID.
     * @param id ID do enfestador
     * @return Detalhes do Enfestador desativado
     */
    @PatchMapping("/{id}")
    public ResponseEntity<EnfestadorOverview> desativarEnfestadorPorId(@PathVariable Long id) {

        EnfestadorOverview response = enfestadorService.desativarEnfestador(id);

        return ResponseEntity.ok(response);
    }
}
