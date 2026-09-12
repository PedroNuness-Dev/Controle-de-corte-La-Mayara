package com.PedroNunesDev.Controle_de_Corte.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    public void enviarEmailTeste(String titulo, String texto, String prioridade) {
        log.warn("Enviando email...");

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            // true = multipart (necessário para HTML e anexos)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailSender);
            helper.setTo("pedrovitornunes89@gmail.com");
            helper.setSubject("[" + formatarPrioridade(prioridade) + "] Suporte | La Mayara Baby");

            // true = indica que o conteúdo é HTML
            helper.setText(buildEmailHtml(titulo, texto, prioridade), true);

            javaMailSender.send(message);

            log.info("Email enviado com sucesso");

        }
        catch (MailSendException e) {
            log.warn("Erro ao enviar email! Verifique conexão com a internet!");
            throw new MailSendException("Sem conexão a internet!");
        }
        catch (Exception e){
            log.warn("Erro interno do servidor: ", e);
        }
    }

    private String buildEmailHtml(String titulo, String texto, String prioridade) {
        String corPrioridade = corDaPrioridade(prioridade);
        String labelPrioridade = formatarPrioridade(prioridade);

        return """
            <!DOCTYPE html>
            <html>
              <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; margin: 0;">
                <div style="
                    max-width: 600px;
                    margin: auto;
                    background: white;
                    border-radius: 10px;
                    overflow: hidden;
                    box-shadow: 0 4px 16px rgba(0,0,0,0.1);
                ">

                  <div style="
                      background: linear-gradient(135deg, #ff7ca7, #ff5c8d);
                      padding: 24px 30px;
                  ">
                    <h1 style="color: white; margin: 0; font-size: 20px;">Controle de Corte</h1>
                    <p style="color: rgba(255,255,255,0.9); margin: 4px 0 0; font-size: 13px;">
                      La Mayara Baby &middot; Nova mensagem de suporte
                    </p>
                  </div>

                  <div style="padding: 28px 30px;">

                    <table style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
                      <tr>
                        <td style="padding: 4px 0; color: #888; font-size: 12px; text-transform: uppercase; letter-spacing: 0.03em;">
                          Prioridade
                        </td>
                      </tr>
                      <tr>
                        <td style="padding: 4px 0 16px;">
                          <span style="
                              display: inline-block;
                              background-color: """ + corPrioridade + """
                              ;
                              color: white;
                              font-size: 13px;
                              font-weight: bold;
                              padding: 6px 14px;
                              border-radius: 999px;
                          ">
                            """ + labelPrioridade + """
                          </span>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding: 4px 0; color: #888; font-size: 12px; text-transform: uppercase; letter-spacing: 0.03em;">
                          Assunto
                        </td>
                      </tr>
                      <tr>
                        <td style="padding: 4px 0; color: #333; font-size: 15px; font-weight: bold;">
                          """ + titulo + """
                        </td>
                      </tr>
                    </table>

                    <p style="color: #888; font-size: 12px; text-transform: uppercase; letter-spacing: 0.03em; margin: 0 0 8px;">
                      Descrição
                    </p>

                    <div style="
                        background-color: #f9f9f9;
                        padding: 16px 18px;
                        border-radius: 8px;
                        border-left: 4px solid """ + corPrioridade + """
                        ;
                        color: #444;
                        font-size: 14px;
                        line-height: 1.6;
                        white-space: pre-wrap;
                    ">
                      """ + texto + """
                    </div>

                  </div>

                  <div style="
                      padding: 16px 30px;
                      background-color: #fafafa;
                      border-top: 1px solid #eee;
                  ">
                    <p style="margin: 0; color: #aaa; font-size: 11px;">
                      Mensagem enviada automaticamente pelo sistema de suporte.
                    </p>
                  </div>

                </div>
              </body>
            </html>
            """;
    }

    private String corDaPrioridade(String prioridade) {
        if (prioridade == null) {
            return "#64748b";
        }

        return switch (prioridade.toUpperCase()) {
            case "BAIXA" -> "#64748b";
            case "MEDIA" -> "#d97706";
            case "ALTA" -> "#ea580c";
            case "URGENTE" -> "#dc2626";
            default -> "#64748b";
        };
    }

    private String formatarPrioridade(String prioridade) {
        if (prioridade == null) {
            return "Não informada";
        }

        return switch (prioridade.toUpperCase()) {
            case "BAIXA" -> "Baixa";
            case "MEDIA" -> "Média";
            case "ALTA" -> "Alta";
            case "URGENTE" -> "Urgente";
            default -> prioridade;
        };
    }
}