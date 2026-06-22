package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.request.SuporteDtoRequest;
import com.PedroNunesDev.Controle_de_Corte.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@CrossOrigin("*")
public class MailSenderController {

    private final MailService mailService;

    public MailSenderController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody SuporteDtoRequest suporteDtoRequest){

        mailService.enviarEmailTeste(suporteDtoRequest.titulo(), suporteDtoRequest.texto());

        return ResponseEntity.ok("Email enviado!");
    }
}
