package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.response.AnaliseCortesResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.QuantidadeCortesMesResponse;
import com.PedroNunesDev.Controle_de_Corte.service.PdfService;
import com.PedroNunesDev.Controle_de_Corte.service.RelatorioService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/relatorio")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final PdfService pdfService;

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

    @GetMapping(value = "/cortes/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> relatorioCortes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        byte[] pdf = pdfService.gerarPdf(dataInicial,dataFinal);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename("relatorio-cortes.pdf").build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
