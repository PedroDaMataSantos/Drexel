package com.damatapedro.controle_gastos.application.dto;

import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InvestimentoResponse {

    Long id();
    String descricao();
    BigDecimal valorAplicado();
    LocalDate data();
    CategoriaInvestimento categoria();
    boolean isAporte();

}
