package com.PedroNunesDev.Controle_de_Corte.service;

import com.PedroNunesDev.Controle_de_Corte.dto.response.*;
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

        return corteRepository.quantidadeCortesRegistrados(diaPrimeiro,diaUltimo);
    }

    public AnaliseCortesResponse buscarAnaliseDeCortesEColaboradores(LocalDate dataInicial, LocalDate dataFinal){

        if (dataInicial.isAfter(dataFinal)) throw new IllegalArgumentException("Data inicial não pode ser posterior a data final");

        return construirAnaliseDeCortes(dataInicial,dataFinal);
    }
    
    public AnaliseCortesResponse construirAnaliseDeCortes(LocalDate dataInicial, LocalDate dataFinal){
        
        QuantidadeCortesMesResponse quantidadeCortesMes = corteRepository.quantidadeCortesRegistrados(dataInicial, dataFinal);
        AnaliseQuantidadePecasCorte quantidadeDePecas = corteRepository.quantidadeDePecasRegistrados(dataInicial,dataFinal);
        List<CortadorOverview> cortadoresAtivos = cortadorService.buscarDetalhesDosCortadoresAtivos(dataInicial,dataFinal);
        List<EnfestadorOverview> enfestadoresAtivos = enfestadorService.buscarDetalhesDosEnfestadoresAtivos(dataInicial,dataFinal);
        List<CortadorOverview> cortadoresInativos = cortadorService.buscarDetalhesDosCortadoresInativos(dataInicial,dataFinal);
        List<EnfestadorOverview> enfestadoresInativos = enfestadorService.buscarDetalhesDosEnfestadoresInativos(dataInicial,dataFinal);

        return new AnaliseCortesResponse(
                dataInicial,
                dataFinal,
                quantidadeCortesMes,
                quantidadeDePecas,
                cortadoresAtivos,
                cortadoresInativos,
                enfestadoresAtivos,
                enfestadoresInativos
        );
    }
}
