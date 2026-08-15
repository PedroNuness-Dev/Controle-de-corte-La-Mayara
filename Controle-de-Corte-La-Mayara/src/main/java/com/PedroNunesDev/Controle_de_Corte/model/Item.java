package com.PedroNunesDev.Controle_de_Corte.model;

import com.PedroNunesDev.Controle_de_Corte.enums.ItemStatus;
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

    private String observacao;

    private Boolean atencao;

    private Integer posicao;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus = ItemStatus.PENDENTE;

    @Builder.Default
    private LocalDate dataDeInsercao = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "lista_id")
    private Lista lista;
}
