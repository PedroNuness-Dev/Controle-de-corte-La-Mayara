package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.response.LoteDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.service.LoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para gerenciar operações relacionadas a Lotes.
 */
@RestController
@RequestMapping("/lote")
@CrossOrigin("*")
public class LoteController {

    private static final Logger logger = LoggerFactory.getLogger(LoteController.class);
    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    /**
     * Busca o lote do ano atual. Se não existir, cria um novo.
     * @return Dados do lote
     */
    @GetMapping("/buscar")
    public ResponseEntity<LoteDtoResponse> buscarLote(){

        logger.debug("Requisição recebida: GET /lote/buscar");

        LoteDtoResponse lote = loteService.buscarLote();

        return ResponseEntity.ok(lote);
    }

    /**
     * Incrementa o número do lote do ano atual.
     * @return Status 200 OK
     */
    @PutMapping("/incrementar")
    public ResponseEntity<Void> incrementarLote(){

        logger.debug("Requisição recebida: PUT /lote/incrementar");

        loteService.incrementarLote();

        return ResponseEntity.ok().build();
    }

    /**
     * Decrementa o número do lote do ano atual.
     * Não permite decrementar abaixo de 1.
     * @return Status 200 OK
     */
    @PutMapping("/decrementar")
    public ResponseEntity<Void> decrementarLote(){

        logger.debug("Requisição recebida: PUT /lote/decrementar");

        loteService.decrementarLote();

        return ResponseEntity.ok().build();
    }
}
