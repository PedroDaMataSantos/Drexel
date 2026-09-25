package com.damatapedro.controle_gastos.application.exception;

public class ClasseCategoriaInvestimentoInvalidoException extends RuntimeException {

    public ClasseCategoriaInvestimentoInvalidoException() {super("Esse tipo de Investimento não é compativel com a categoria selecionada");}

    public ClasseCategoriaInvestimentoInvalidoException(String message) {
        super(message);
    }
}
