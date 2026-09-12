package com.PedroNunesDev.Controle_de_Corte.repository;

import com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorOverview;
import com.PedroNunesDev.Controle_de_Corte.model.Enfestador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnfestadorRepository extends JpaRepository<Enfestador,Long> {

    @Query("""
    SELECT new com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorOverview(
        enfestador.id,
        enfestador.nome,
        COUNT(c),
        enfestador.ativo,
        enfestador.dataDeCadastro
    )
    FROM Enfestador enfestador
    LEFT JOIN enfestador.cortes c
    WHERE enfestador.ativo = TRUE
    GROUP BY enfestador.nome
""")
    List<EnfestadorOverview> buscarEnfestadoresESuasQuantidadesDeCortes();

    @Query("""
    SELECT enfestador FROM Enfestador enfestador
    WHERE enfestador.ativo = TRUE
    ORDER BY enfestador.nome
""")
    List<Enfestador> buscarEnfestadoresAtivos();

    @Query("""
    SELECT new com.PedroNunesDev.Controle_de_Corte.dto.response.EnfestadorOverview(
        enfestador.id,
        enfestador.nome,
        COUNT(c),
        enfestador.ativo,
        enfestador.dataDeCadastro
    )
    FROM Enfestador enfestador
    LEFT JOIN enfestador.cortes c
    WHERE enfestador.id = :id
    GROUP BY enfestador.nome
""")
    Optional<EnfestadorOverview> buscarEnfestadorPorId(@Param("id") Long id);
}
