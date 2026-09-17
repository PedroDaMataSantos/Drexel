package com.damatapedro.controle_gastos.application.dto;

import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoRequest(

        String descricao,
        BigDecimal valorAplicado,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate data,
        CategoriaInvestimento categoria,
        boolean isAporte
) {
}
