package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.response.QuantidadeCortesMesResponse;
import com.PedroNunesDev.Controle_de_Corte.repository.CorteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final CorteRepository corteRepository;

    public QuantidadeCortesMesResponse buscarCortesRegistradosNoMes(Integer mes, Integer ano){

        Assert.notNull(ano, "O ano para busca não pode ser nulo");

        Integer mesParaBuscar = verificarMes(mes);

        LocalDate diaPrimeiro = LocalDate.of(ano, mesParaBuscar, 1);
        LocalDate diaUltimo = diaPrimeiro.withDayOfMonth(diaPrimeiro.lengthOfMonth());

        Integer quantidadeCortesRegistrados = corteRepository.quantidadeCortesRegistradosNoMes(diaPrimeiro,diaUltimo);

        return new QuantidadeCortesMesResponse(quantidadeCortesRegistrados);
    }

    private Integer verificarMes(Integer mes){

        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês inválido. O valor deve estar entre 1 e 12.");
        }
        return mes;
    }
}
