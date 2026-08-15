package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.request.ListaDTORequest;
import com.PedroNunesDev.Controle_de_Corte.model.Item;
import com.PedroNunesDev.Controle_de_Corte.model.Lista;
import com.PedroNunesDev.Controle_de_Corte.repository.ListaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListaService {

    private final ListaRepository listaRepository;

    @Transactional
    public void registerLista(ListaDTORequest listaDTORequest){

        if (listaDTORequest.items().isEmpty()) throw new IllegalArgumentException("A lista deve conter pelo menos 1 item");

        Lista listaParaSalvar = Lista.builder()
                .titulo(listaDTORequest.titulo())
                .descricao(listaDTORequest.descricao())
                .build();

        List<Item> itemsParaSalvar = listaDTORequest.items()
                .stream()
                .map(item -> Item.builder()
                        .nome(item.nome())
                        .quantidade(item.quantidade())
                        .observacao(item.observacao())
                        .atencao(item.atencao())
                        .posicao(item.posicao())
                        .lista(listaParaSalvar)
                        .build())
                .toList();

        listaParaSalvar.setItems(itemsParaSalvar);

        listaRepository.save(listaParaSalvar);
    }
}
