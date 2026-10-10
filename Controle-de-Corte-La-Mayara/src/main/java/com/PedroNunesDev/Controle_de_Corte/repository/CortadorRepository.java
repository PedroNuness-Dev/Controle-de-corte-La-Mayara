package com.PedroNunesDev.Controle_de_Corte.repository;

import com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview;
import com.PedroNunesDev.Controle_de_Corte.model.Cortador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CortadorRepository extends JpaRepository<Cortador, Long> {

    @Query("""
    SELECT new com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview(
        cortador.id,
        cortador.nome,
        COUNT(c),
        cortador.ativo,
        cortador.dataDeCadastro
    )
    FROM Cortador cortador
    LEFT JOIN cortador.cortes c
    WHERE cortador.ativo = TRUE
    GROUP BY cortador.id
    ORDER BY cortador.nome
""")
    List<CortadorOverview> buscarCortadoresESuasQuantidadesDeCortes();

    @Query("""
    SELECT new com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview(
        cortador.id,
        cortador.nome,
        COUNT(c),
        cortador.ativo,
        cortador.dataDeCadastro
    )
    FROM Cortador cortador
    LEFT JOIN cortador.cortes c
    ON c.dataDeRegistro BETWEEN :dataInicial AND :dataFinal
    WHERE cortador.ativo = TRUE
    GROUP BY cortador.id
    ORDER BY cortador.nome
""")
    List<CortadorOverview> buscarCortadoresESuasQuantidadesDeCortesPorData(@Param("dataInicial") LocalDate dataIncial, @Param("dataFinal") LocalDate dataFinal);

    @Query("""
    SELECT new com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview(
        cortador.id,
        cortador.nome,
        COUNT(c),
        cortador.ativo,
        cortador.dataDeCadastro
    )
    FROM Cortador cortador
    LEFT JOIN cortador.cortes c
    ON c.dataDeRegistro BETWEEN :dataInicial AND :dataFinal
    WHERE cortador.ativo = FALSE
    GROUP BY cortador.id
    ORDER BY cortador.nome
""")
    List<CortadorOverview> buscarCortadoresInativosESuasQuantidadesDeCortesPorData(@Param("dataInicial") LocalDate dataIncial, @Param("dataFinal") LocalDate dataFinal);

    @Query("""
    SELECT new com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview(
        cortador.id,
        cortador.nome,
        COUNT(c),
        cortador.ativo,
        cortador.dataDeCadastro
    )
    FROM Cortador cortador
    LEFT JOIN cortador.cortes c
    WHERE cortador.ativo = FALSE
    GROUP BY cortador.id
    ORDER BY cortador.nome
""")
    List<CortadorOverview> buscarCortadoresInativosESuasQuantidadesDeCortes();

    @Query("""
    SELECT cortador FROM Cortador cortador
    WHERE cortador.ativo = TRUE
    ORDER BY cortador.nome
""")
    List<Cortador> buscarCortadoresAtivos();

    @Query("""
    SELECT new com.PedroNunesDev.Controle_de_Corte.dto.response.CortadorOverview(
        cortador.id,
        cortador.nome,
        COUNT(c),
        cortador.ativo,
        cortador.dataDeCadastro
    )
    FROM Cortador cortador
    LEFT JOIN cortador.cortes c
    WHERE cortador.id = :id
    GROUP BY cortador.id
    ORDER BY cortador.nome
""")
    Optional<CortadorOverview> buscarCortadorPorId(@Param("id") Long id);
}
