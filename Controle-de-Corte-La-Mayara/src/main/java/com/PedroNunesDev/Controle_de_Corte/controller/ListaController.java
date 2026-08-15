package com.PedroNunesDev.Controle_de_Corte.controller;

import com.PedroNunesDev.Controle_de_Corte.dto.request.ListaDTORequest;
import com.PedroNunesDev.Controle_de_Corte.service.ListaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lista")
@RequiredArgsConstructor
@Tag(name = "Lista Controller", description = "Responsável pelas ações relacionadas as listas de cortes")
public class ListaController {

    private final ListaService listaService;

    @PostMapping
    public ResponseEntity<Void> registerLista(@RequestBody ListaDTORequest listaDTORequest){

        listaService.registerLista(listaDTORequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
