package com.damatapedro.controle_gastos.application.exception;

public class CategoriaRegistroInvalidoException extends RuntimeException {

    public CategoriaRegistroInvalidoException() {super("Esse tipo de Registro não é compativel com a categoria selecionada");}

    public CategoriaRegistroInvalidoException(String message) {
        super(message);
    }
}
