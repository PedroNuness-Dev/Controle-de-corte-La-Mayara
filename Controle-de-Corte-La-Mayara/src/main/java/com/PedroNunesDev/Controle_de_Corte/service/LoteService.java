package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.response.LoteDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.exception.InvalidOperationException;
import com.PedroNunesDev.Controle_de_Corte.exception.ResourceNotFoundException;
import com.PedroNunesDev.Controle_de_Corte.model.Lote;
import com.PedroNunesDev.Controle_de_Corte.repository.LoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class LoteService {

    private static final Logger logger = LoggerFactory.getLogger(LoteService.class);
    private final LoteRepository loteRepository;

    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @Transactional(readOnly = true)
    public LoteDtoResponse buscarLote(){

        logger.info("Buscando lote do ano atual");

        int anoAtual = LocalDate.now().getYear();

        Optional<Lote> loteBuscado = loteRepository.findByAno(anoAtual);

        //Verifica se existe um lote pelo ano
        if (loteBuscado.isPresent()){

            Lote loteExistente = loteBuscado.get();

            logger.debug("Lote encontrado: {}/{}", loteExistente.getNumero_lote(), loteExistente.getAno());

            String loteFormatado = (loteExistente.getNumero_lote()+"/"+loteExistente.getAno());

            LoteDtoResponse loteDeResponse = new LoteDtoResponse(loteExistente.getId(),loteFormatado, loteExistente.getAno());

            return loteDeResponse;
        } //Se não existir cria um com o ano atual
        else{

            logger.info("Lote não encontrado para o ano {}. Criando novo lote", anoAtual);

            Lote novoLote = new Lote(null, 1, LocalDate.now().getYear());

            Lote novoLoteSalvo = loteRepository.save(novoLote);

            logger.info("Novo lote criado com sucesso: {}/{}", novoLoteSalvo.getNumero_lote(), novoLoteSalvo.getAno());

            String loteFormatado = (novoLoteSalvo.getNumero_lote()+"/"+novoLoteSalvo.getAno());

            LoteDtoResponse loteDtoResponse = new LoteDtoResponse(novoLoteSalvo.getId(), loteFormatado, novoLoteSalvo.getAno());

            return loteDtoResponse;
        }
    }

    public Lote buscarLoteModel(){

        int anoAtual = LocalDate.now().getYear();

        return loteRepository.findByAno(anoAtual)
                .orElseThrow(() -> new ResourceNotFoundException("Lote não encontrado para o ano " + anoAtual));
    }

    @Transactional
    public void incrementarLote(){

        logger.info("Incrementando lote do ano atual");

        Lote lote = loteRepository.findByAno(LocalDate.now().getYear())
                .orElseThrow(() -> new ResourceNotFoundException("Lote não encontrado para o ano " + LocalDate.now().getYear()));

        lote.incrementarLote();
        loteRepository.save(lote);

        logger.info("Lote incrementado com sucesso para: {}/{}", lote.getNumero_lote(), lote.getAno());
    }

    @Transactional
    public void decrementarLote(){

        logger.info("Decrementando lote do ano atual");

        Lote lote = loteRepository.findByAno(LocalDate.now().getYear())
                .orElseThrow(() -> new ResourceNotFoundException("Lote não encontrado para o ano " + LocalDate.now().getYear()));

        if(lote.getNumero_lote() <= 1){
            logger.warn("Tentativa de decrementar lote abaixo do mínimo permitido (1)");
            throw new InvalidOperationException("Não é permitido decrementar o lote abaixo de 1");
        }

        lote.decrementarLote();
        loteRepository.save(lote);

        logger.info("Lote decrementado com sucesso para: {}/{}", lote.getNumero_lote(), lote.getAno());
    }
}
