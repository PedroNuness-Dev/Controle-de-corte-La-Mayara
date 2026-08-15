package com.PedroNunesDev.Controle_de_Corte.enums;

public enum ItemStatus {

    PENDENTE("PENDENTE"),
    CONCLUIDO("CONCLUIDO");

    private final String status;

    ItemStatus(String status) {
        this.status = status;
    }

    public static ItemStatus from(String status){

        for (ItemStatus itemStatus : values()){
            if (itemStatus.status.equalsIgnoreCase(status)){
                return itemStatus;
            }
        }

        throw new IllegalArgumentException("Status de item não encontrado");
    }
}
