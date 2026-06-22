package com.PedroNunesDev.Controle_de_Corte.mapper;

import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.model.Cortador;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CortadorMapper {

    CortadorDtoResponse toDto(Cortador cortador);
}
