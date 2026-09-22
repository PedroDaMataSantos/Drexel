package com.damatapedro.controle_gastos.application.exception;

public class TipoInvestimentoInvalidoException extends RuntimeException {

    public TipoInvestimentoInvalidoException() {
        super("Tipo de investimento Invalido");
    }

    public TipoInvestimentoInvalidoException(String message) {
        super(message);
    }
}
