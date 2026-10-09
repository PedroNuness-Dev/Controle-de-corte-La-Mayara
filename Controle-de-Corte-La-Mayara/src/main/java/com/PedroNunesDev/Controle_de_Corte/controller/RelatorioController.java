package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.response.AnaliseCortesResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.QuantidadeCortesMesResponse;
import com.PedroNunesDev.Controle_de_Corte.service.RelatorioService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @GetMapping("/analise")
    public ResponseEntity<AnaliseCortesResponse> buscarAnaliseMensal(
            @Parameter(
                    description = "Data inicial para realizar a análise de cortes",
                    example = "2026-01-01"
            )
            @RequestParam LocalDate dataInicial,
            @Parameter(
                    description = "Data final para realizar a análise de cortes",
                    example = "2026-01-01"
            )
            @RequestParam LocalDate dataFinal
    ){

        AnaliseCortesResponse analiseCortesResponse = relatorioService.buscarAnaliseDeCortesEColaboradores(dataInicial, dataFinal);

        return ResponseEntity.ok(analiseCortesResponse);
    }
}
