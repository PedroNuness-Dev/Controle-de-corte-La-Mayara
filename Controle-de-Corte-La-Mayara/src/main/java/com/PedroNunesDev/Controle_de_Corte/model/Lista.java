package com.PedroNunesDev.Controle_de_Corte.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String descricao;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    @Builder.Default
    private LocalDate dataDeCriacao = LocalDate.now();
}
