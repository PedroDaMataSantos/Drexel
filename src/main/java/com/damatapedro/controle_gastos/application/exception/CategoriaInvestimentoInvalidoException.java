package com.damatapedro.controle_gastos.application.exception;

public class CategoriaInvestimentoInvalidoException extends RuntimeException {

    public CategoriaInvestimentoInvalidoException() {super("Esse tipo de Investimento não é compativel com a categoria selecionada");}

    public CategoriaInvestimentoInvalidoException(String message) {
        super(message);
    }
}
