package com.PedroNunesDev.Controle_de_Corte.repository;

import com.PedroNunesDev.Controle_de_Corte.enums.CorteStatus;
import com.PedroNunesDev.Controle_de_Corte.model.Corte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CorteRepository extends JpaRepository<Corte,Long> {

    @Query("SELECT c FROM Corte c" +
            " WHERE c.dataDeRegistro BETWEEN :diaPrimeiro AND :diaUltimo" +
            " ORDER BY c.nomeModelo ASC")
    List<Corte> buscarPorMes(@Param("diaPrimeiro") LocalDate diaPrimeiro,@Param("diaUltimo") LocalDate diaUltimo);

    @Query("SELECT c FROM Corte c" +
            " WHERE c.dataDeRegistro BETWEEN :diaPrimeiro AND :diaUltimo" +
            " AND c.corteStatus = :status" +
            " ORDER BY c.nomeModelo ASC")
    List<Corte> buscarPorMesEPorStatus(@Param("diaPrimeiro") LocalDate diaPrimeiro, @Param("diaUltimo") LocalDate diaUltimo, @Param("status") CorteStatus status);

    @Query("SELECT c FROM Corte c" +
            " WHERE c.dataDeRegistro BETWEEN :diaPrimeiro AND :diaUltimo" +
            " AND (LOWER(c.nomeModelo) LIKE CONCAT(LOWER(:nome), '%') " +
            " OR LOWER(c.loteFormatado) LIKE CONCAT(LOWER(:nome), '%'))" +
            " ORDER BY c.nomeModelo ASC")
    List<Corte> buscarPorNomeOuLote(@Param("nome") String nome, @Param("diaPrimeiro") LocalDate diaPrimeiro, @Param("diaUltimo") LocalDate diaUltimo);

    @Query("""
    SELECT 
       COALESCE(SUM(CASE WHEN LOWER(c.enfestador.nome) = LOWER(:nome) THEN 1 ELSE 0 END),0)
    FROM Corte c
""")
    Long buscarQuantidadeDeCortesPorEnfestador(@Param("nome") String nome);

    @Query("""
    SELECT 
       COALESCE(SUM(CASE WHEN LOWER(c.cortador.nome) = LOWER(:nome) THEN 1 ELSE 0 END),0)
    FROM Corte c
""")
    Long buscarQuantidadeDeCortesPorCortador(@Param("nome") String nome);
}
