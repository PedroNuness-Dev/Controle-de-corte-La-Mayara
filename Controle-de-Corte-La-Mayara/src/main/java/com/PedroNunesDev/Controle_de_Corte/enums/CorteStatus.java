package com.PedroNunesDev.Controle_de_Corte.enums;

public enum CorteStatus {

    PENDENTE("PENDENTE"),
    ENFESTADO("ENFESTADO"),
    CORTADO("CORTADO"),
    CANCELADO("CANCELADO");

    public String status;

    CorteStatus(String status) {
        this.status = status;
    }

    public static CorteStatus from(String tipoStatus){

        CorteStatus corteStatus = null;

        for (CorteStatus c : values()){

            if (c.status.equalsIgnoreCase(tipoStatus)){
                corteStatus = c;
                break;
            }
        }

        if (corteStatus == null){
            throw new IllegalArgumentException("Status de corte inválido: " + tipoStatus);
        }

        return corteStatus;
    }
}
