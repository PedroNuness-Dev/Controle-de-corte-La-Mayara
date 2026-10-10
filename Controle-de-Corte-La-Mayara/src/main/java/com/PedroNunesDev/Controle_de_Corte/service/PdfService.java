package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.response.AnaliseCortesResponse;
import com.PedroNunesDev.Controle_de_Corte.utils.ValidacaoDatas;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;

@Service
public class PdfService {

    private static final String TEMPLATE = "relatorio-cortes"; // templates/relatorio-cortes.html
    private static final ZoneId FUSO_BRASIL = ZoneId.of("America/Sao_Paulo");
    private static final String LOGO = "relatorio/logo.png";   // src/main/resources/relatorio/logo.png
    private final RelatorioService relatorioService;
    private final ValidacaoDatas validacaoDatas;

    private final TemplateEngine templateEngine;
    private final String logoBase64; // lido uma vez só, na subida da aplicação

    public PdfService(TemplateEngine templateEngine, RelatorioService relatorioService, ValidacaoDatas validacaoDatas) {
        this.validacaoDatas = validacaoDatas;
        this.relatorioService = relatorioService;
        this.templateEngine = templateEngine;
        this.logoBase64 = carregarLogo();
    }

    /** Passo 1 + 2 + 3: dados -> HTML -> PDF. */
    public byte[] gerarPdf(LocalDate dataInicial, LocalDate dataFinal) {

        validacaoDatas.validarDatas(dataInicial,dataFinal);

        AnaliseCortesResponse dados = relatorioService.buscarAnaliseDeCortesEColaboradores(dataInicial, dataFinal);
        String html = renderizarHtml(dados);
        return converterParaPdf(html);
    }

    // Thymeleaf: preenche o template com os dados e devolve uma String HTML
    private String renderizarHtml(AnaliseCortesResponse dados) {
        Context context = new Context();
        context.setVariable("dados", dados);
        context.setVariable("logoBase64", logoBase64);
        context.setVariable("dataEmissao", LocalDate.now(FUSO_BRASIL));
        return templateEngine.process(TEMPLATE, context);
    }

    // OpenHTMLtoPDF: transforma o HTML em bytes de PDF
    private byte[] converterParaPdf(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null); // null: não há recursos externos (logo vai embutida)
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao gerar o PDF do relatório de cortes", e);
        }
    }

    private String carregarLogo() {
        try (var in = new ClassPathResource(LOGO).getInputStream()) {
            return Base64.getEncoder().encodeToString(in.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Logo não encontrada em " + LOGO, e);
        }
    }
}
