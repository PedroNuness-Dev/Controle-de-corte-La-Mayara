package com.PedroNunesDev.Controle_de_Corte.model;

import com.PedroNunesDev.Controle_de_Corte.enums.CorteStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "corte")
public class Corte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corte")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "data_de_corte")
    private LocalDate dataDeCorte;

    @Column(name = "data_de_registro")
    private LocalDate dataDeRegistro;

    @Column(name = "nome_modelo")
    private String nomeModelo;

    @Column(name = "quantidade_total")
    private Integer quantidadeTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "corte_status")
    private CorteStatus corteStatus;

    @Column(name = "lote_formatado")
    private String loteFormatado;

    @JoinColumn(name = "lote")
    @ManyToOne
    private Lote lote;

    @JoinColumn(name = "id_enfestador")
    @ManyToOne
    private Enfestador enfestador;

    @JoinColumn(name = "id_cortador")
    @ManyToOne
    private Cortador cortador;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;
}
