package com.PedroNunesDev.Controle_de_Corte.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Integer quantidade;

    private String oberservacao;

    private Boolean atencao;

    private Integer posicao;

    @Builder.Default
    private LocalDate dataDeInsercao = LocalDate.now();

    @ManyToOne
    @JoinColumn
    private Lista lista;
}
