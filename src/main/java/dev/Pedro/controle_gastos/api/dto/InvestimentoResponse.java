package dev.Pedro.controle_gastos.api.dto;

import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;

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
