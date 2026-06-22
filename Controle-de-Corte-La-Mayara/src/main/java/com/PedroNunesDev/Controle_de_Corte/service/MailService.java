package com.PedroNunesDev.Controle_de_Corte.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;


@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Value("${spring.mail.username}")
    private String emailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String enviarEmailTeste(String titulo, String texto) {
        logger.warn("Enviando email...");

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            // true = multipart (necessário para HTML e anexos)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailSender);
            helper.setTo("pedrovitornunes89@gmail.com");
            helper.setSubject("Suporte do Controle de corte | La Mayara Baby");

            // true = indica que o conteúdo é HTML
            helper.setText(buildEmailHtml(titulo, texto), true);

            javaMailSender.send(message);

            logger.info("Email enviado com sucesso");
            return "Email enviado com sucesso";

        }
        catch (MailSendException e) {
            logger.warn("Erro ao enviar email! Verifique conexão com a internet!");
            throw new MailSendException("Sem conexão a internet!");
        }
        catch (Exception e){
            logger.warn("Erro interno do servidor: ", e);
            return "Erro no servidor!";
        }
    }

    private String buildEmailHtml(String titulo, String texto) {
        return """
            <!DOCTYPE html>
            <html>
              <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="
                    max-width: 600px;
                    margin: auto;
                    background: white;
                    border-radius: 8px;
                    padding: 30px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                ">

                  <h1 style="color: #333;">Olá, Pedro!</h1>

                  <p style="color: #666;">
                    Esta é uma mensagem de suporte do
                    <strong>Controle de Corte | La Mayara Baby</strong>
                  </p>

                  <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">

                  <p>
                    <strong>Assunto:</strong>
                    """ + titulo + """
                  </p>

                  <p>
                    <strong>Descrição:</strong>
                  </p>

                  <div style="
                      background-color: #f9f9f9;
                      padding: 15px;
                      border-radius: 6px;
                      border-left: 4px solid #ff5c8d;
                      color: #444;
                      white-space: pre-wrap;
                  ">
                    """ + texto + """
                  </div>

                </div>
              </body>
            </html>
            """;
    }
}