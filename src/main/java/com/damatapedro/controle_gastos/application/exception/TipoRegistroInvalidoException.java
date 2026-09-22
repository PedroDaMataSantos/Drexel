package com.damatapedro.controle_gastos.application.exception;

public class TipoRegistroInvalidoException extends RuntimeException {

    public TipoRegistroInvalidoException() {
        super("Essa categoria é incompatível com o tipo de registro selecionado");
    }
    public TipoRegistroInvalidoException(String message) {
        super(message);
    }
}
