package com.PedroNunesDev.Controle_de_Corte.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enfestador")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Enfestador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_enfestador")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nome;

    private Boolean ativo;

    @Builder.Default
    private LocalDate dataDeCadastro = LocalDate.now();

    @OneToMany(mappedBy = "enfestador", fetch = FetchType.LAZY)
    private List<Corte> cortes = new ArrayList<>();

    public Integer getQuantidadeDeCortesEnfestados(){
        return cortes.size();
    }
}
