package com.PedroNunesDev.Controle_de_Corte.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cortador")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cortador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cortador")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nome;

    private Boolean ativo;

    @OneToMany(mappedBy = "cortador", fetch = FetchType.LAZY)
    private List<Corte> cortes = new ArrayList<>();

    public Integer getQuantidadeDeCortes(){
        return cortes.size();
    }
}
