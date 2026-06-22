package com.PedroNunesDev.Controle_de_Corte.exception;

/**
 * Exceção lançada quando uma operação inválida é realizada.
 */
public class InvalidOperationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

