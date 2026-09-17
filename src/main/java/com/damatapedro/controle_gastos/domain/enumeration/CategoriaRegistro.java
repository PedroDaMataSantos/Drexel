package com.damatapedro.controle_gastos.domain.enumeration;

public enum CategoriaRegistro {

    // GASTOS
    ALIMENTACAO("Alimentação", TipoRegistro.SAIDA),
    LAZER("Lazer", TipoRegistro.SAIDA),
    COMBUSTIVEL("Combustível", TipoRegistro.SAIDA),
    SAUDE("Saúde", TipoRegistro.SAIDA),
    EDUCACAO("Educação", TipoRegistro.SAIDA),
    FATURA_CARTAO("Fatura do cartão", TipoRegistro.SAIDA),
    ALUGUEL("Aluguel", TipoRegistro.SAIDA),
    CONTAS("Contas", TipoRegistro.SAIDA),
    OUTROS("Outros gastos", TipoRegistro.SAIDA),
    TRIBUTO("Impostos", TipoRegistro.SAIDA),

    // RECEITAS
    SALARIO("Salário", TipoRegistro.ENTRADA),
    INVESTIMENTO("Investimento", TipoRegistro.ENTRADA),
    VALES("Vales", TipoRegistro.ENTRADA),
    BENEFICIO("Benefícios", TipoRegistro.ENTRADA),
    OUTRAS("Outras receitas", TipoRegistro.ENTRADA);


    private final String descricao;
    private final TipoRegistro tipo;


    CategoriaRegistro(String descricao, TipoRegistro tipo) {
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRegistro getTipo() {
        return tipo;
    }
}