package com.PedroNunesDev.Controle_de_Corte.repository;

import com.PedroNunesDev.Controle_de_Corte.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Long> {
}
