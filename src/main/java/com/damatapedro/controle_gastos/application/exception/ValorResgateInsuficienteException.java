package com.damatapedro.controle_gastos.application.exception;

public class ValorResgateInsuficienteException extends RuntimeException {

    public ValorResgateInsuficienteException() {
        super("Valor disponível para resgate é menor que o valor solicitado");
    }

    public ValorResgateInsuficienteException(String message) {
        super(message);
    }
}
