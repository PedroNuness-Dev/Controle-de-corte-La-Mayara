package com.PedroNunesDev.Controle_de_Corte.mapper;

import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.model.Enfestador;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnfestadorMapper {

    EnfestadorDtoResponse toDto(Enfestador enfestador);
}
