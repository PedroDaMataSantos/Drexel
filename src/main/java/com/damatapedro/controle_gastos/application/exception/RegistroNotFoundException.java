package com.damatapedro.controle_gastos.application.exception;

public class RegistroNotFoundException extends RuntimeException {

    public RegistroNotFoundException() {
        super("Registro não encontrado");
    }

    public RegistroNotFoundException(String message) {
        super(message);
    }
}
