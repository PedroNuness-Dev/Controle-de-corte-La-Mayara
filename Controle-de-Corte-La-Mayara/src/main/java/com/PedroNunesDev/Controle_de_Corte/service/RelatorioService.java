package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.response.AnaliseCortesResponse;
import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview;
import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorOverview;
import com.PedroNunesDev.Controle_de_Corte.dto.response.QuantidadeCortesMesResponse;
import com.PedroNunesDev.Controle_de_Corte.repository.CorteRepository;
import com.PedroNunesDev.Controle_de_Corte.utils.ValidacaoDatas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final CorteRepository corteRepository;
    private final ValidacaoDatas validacaoDatas;
    private final CortadorService cortadorService;
    private final EnfestadorService enfestadorService;

    public QuantidadeCortesMesResponse buscarCortesRegistradosNoMes(Integer mes, Integer ano){

        validacaoDatas.validarPeriodo(mes,ano);

        LocalDate diaPrimeiro = LocalDate.of(ano, mes, 1);
        LocalDate diaUltimo = diaPrimeiro.withDayOfMonth(diaPrimeiro.lengthOfMonth());

        return corteRepository.quantidadeCortesRegistradosNoMes(diaPrimeiro,diaUltimo);
    }

    public AnaliseCortesResponse buscarAnaliseDeCortesEColaboradores(LocalDate dataInicial, LocalDate dataFinal){

        if (dataInicial.isAfter(dataFinal)) throw new IllegalArgumentException("Data inicial não pode ser posterior a data final");

        QuantidadeCortesMesResponse quantidadeCortesMes = corteRepository.quantidadeCortesRegistradosNoMes(dataInicial, dataFinal);
        List<CortadorOverview> cortadorOverview = cortadorService.buscarDetalhesDosCortadoresAtivos(dataInicial,dataFinal);
        List<EnfestadorOverview> enfestadorOverview = enfestadorService.buscarDetalhesDosEnfestadoresAtivos(dataInicial,dataFinal);

        return new AnaliseCortesResponse(
                dataInicial,
                dataFinal,
                quantidadeCortesMes,
                cortadorOverview,
                enfestadorOverview
        );
    }
}
