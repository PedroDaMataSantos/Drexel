package dev.Pedro.controle_gastos.enums;

public enum CategoriaInvestimento {

    // RECEITAS
    CDB("CDB",false),
    LCI("LCI",true),
    LCA("LCA",true),
    POUPANCA("Poupança",true),
    OUTROS("Outros Investimentos",false);


    private final String descricao;
    private final boolean isIsento;


    CategoriaInvestimento(String descricao,boolean isIsentoIR) {
        this.descricao = descricao;
        this.isIsento = isIsentoIR;


    }

    public String getDescricao() {
        return descricao;
    }

}

