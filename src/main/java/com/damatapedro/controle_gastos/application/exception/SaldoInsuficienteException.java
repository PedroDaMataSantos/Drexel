package com.damatapedro.controle_gastos.application.exception;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException() {
        super("Saldo insuficiente");
    }

    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
