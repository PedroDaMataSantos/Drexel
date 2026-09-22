package com.damatapedro.controle_gastos.application.exception;

public class InvestimentoNotFoundException extends RuntimeException {

    public InvestimentoNotFoundException(){super("Investimento não encontrado!");}

    public InvestimentoNotFoundException(String message) {
        super(message);
    }
}
