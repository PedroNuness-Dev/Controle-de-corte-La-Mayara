package com.PedroNunesDev.Controle_de_Corte.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "lote")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Long id;

    @Column(name = "numero_lote", nullable = false)
    @Min(1)
    private Integer numero_lote;

    @Column(name = "ano_lote", nullable = false)
    @NotNull
    private Integer ano;

    @OneToMany(mappedBy = "lote", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Corte> cortes = new ArrayList<>();

    public Lote(Long id, Integer numero_lote, Integer ano) {
        this.id = id;
        this.numero_lote = numero_lote;
        this.ano = ano;
    }

    public void incrementarLote(){
        this.numero_lote++;
    }

    public void decrementarLote(){
        this.numero_lote--;
    }

    public String formatarLote(){

        return (numero_lote+"/"+ano);
    }
}
