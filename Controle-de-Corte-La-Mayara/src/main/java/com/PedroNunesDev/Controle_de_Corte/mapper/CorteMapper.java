package com.PedroNunesDev.Controle_de_Corte.mapper;

import com.PedroNunesDev.Controle_de_Corte.dto.response.CorteDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.model.Corte;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {LoteMapper.class, CortadorMapper.class, EnfestadorMapper.class})
public interface CorteMapper {

    CorteDtoResponse toDto(Corte corte);
}
