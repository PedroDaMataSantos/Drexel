package com.damatapedro.controle_gastos.application.exception;

public class ValorInvalidoException extends RuntimeException {

    public ValorInvalidoException(){super("O valor deve ser positivo");}

    public ValorInvalidoException(String message) {
        super(message);
    }
}
