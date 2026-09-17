package com.damatapedro.controle_gastos.domain.enumeration;

public enum TipoRegistro {
    SAIDA("Saída"),
    ENTRADA("Entrada");

    private final String descricao;

    TipoRegistro(String descricao) {

        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
