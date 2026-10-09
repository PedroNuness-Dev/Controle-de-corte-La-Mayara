package com.PedroNunesDev.Controle_de_Corte.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.time.YearMonth;

@Component
@Slf4j
public class ValidacaoDatas {

    public void validarPeriodo(Integer month, Integer year){

        validarMes(month);
        validarAno(year);

        YearMonth requestedPeriod = YearMonth.of(year, month);
        if (requestedPeriod.isAfter(YearMonth.now())) {
            log.warn("Requested period is in the future: {}/{}", month, year);
            throw new IllegalArgumentException("The requested period is in the future.");
        }

        log.debug("Period validated: {}/{}", month, year);
    }

    public void validarMes(Integer month){
        if (month == null || month < 1 || month > 12) {
            log.warn("Invalid month received: {}", month);
            throw new IllegalArgumentException("Invalid month. The value must be between 1 and 12.");
        }
    }

    public void validarAno(Integer year){


        if (year == null){
            log.warn("Null year received in movement search");
            throw new IllegalArgumentException("The year for the search cannot be null");
        }
        if ( year > Year.now().getValue()){
            log.warn("Year outside allowed range. year={}, currentYear={}",
                    year, Year.now().getValue());
            throw new IllegalArgumentException("Invalid year for search: the year cannot be in the future or before the user's creation date.");
        }
    }
}
