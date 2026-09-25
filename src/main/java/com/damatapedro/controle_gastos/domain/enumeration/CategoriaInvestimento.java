package com.damatapedro.controle_gastos.domain.enumeration;

import com.damatapedro.controle_gastos.domain.entity.Investimento;
import com.damatapedro.controle_gastos.domain.entity.RendaFixa;
import com.damatapedro.controle_gastos.domain.entity.RendaVariavel;
import lombok.Getter;

@Getter
public enum CategoriaInvestimento {

    CDB(
            "CDB",
            false,
            RendaFixa.class
    ),
    LCI(
            "LCI",
            true,
            RendaFixa.class
    ),
    LCA(
            "LCA",
            true,
            RendaFixa.class
    ),
    POUPANCA(
            "Poupança",
            true,
            RendaFixa.class
    ),

    ACAO(
            "Ação",
            false,
            RendaVariavel.class
    ),
    FII(
            "Fundo imobiliário",
            false,
            RendaVariavel.class
    );

    private final String descricao;
    private final boolean isento;
    private final Class<? extends Investimento> tipoInvestimento;

    CategoriaInvestimento(
            String descricao,
            boolean isento,
            Class<? extends Investimento> tipoInvestimento
    ) {
        this.descricao = descricao;
        this.isento = isento;
        this.tipoInvestimento = tipoInvestimento;
    }

}

