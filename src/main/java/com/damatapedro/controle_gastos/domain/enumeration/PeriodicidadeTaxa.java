package com.damatapedro.controle_gastos.domain.enumeration;

public enum PeriodicidadeTaxa {

    MENSAL ( "Mensal"),
    ANUAL( "Anual");


    private final String descricao;

    PeriodicidadeTaxa(String descricao) {
        this.descricao = descricao;
    }
}
