package com.PedroNunesDev.Controle_de_Corte.mapper;

import com.PedroNunesDev.Controle_de_Corte.dto.response.LoteDtoResponse;
import com.PedroNunesDev.Controle_de_Corte.model.Lote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoteMapper {

    LoteDtoResponse toDto(Lote lote);
}
