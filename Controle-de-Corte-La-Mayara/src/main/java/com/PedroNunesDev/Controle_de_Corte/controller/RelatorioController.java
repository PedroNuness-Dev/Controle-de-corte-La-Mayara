package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.response.QuantidadeCortesMesResponse;
import com.PedroNunesDev.Controle_de_Corte.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorio")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/quantidade/registros")
    public ResponseEntity<QuantidadeCortesMesResponse> buscarQuantidadeCortesRegistradosMes(@RequestParam Integer mes, @RequestParam Integer ano){

        QuantidadeCortesMesResponse quantidadeCortesMesResponse = relatorioService.buscarCortesRegistradosNoMes(mes,ano);

        return ResponseEntity.ok(quantidadeCortesMesResponse);
    }
}
