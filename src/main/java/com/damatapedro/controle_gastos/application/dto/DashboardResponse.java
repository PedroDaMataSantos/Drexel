package com.damatapedro.controle_gastos.application.dto;

import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaRegistro;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardResponse(
        BigDecimal saldoTotal,
        BigDecimal saldoMensal,
        BigDecimal gastosTotal,
        BigDecimal gastosMensal,
        BigDecimal receitaTotal,
        BigDecimal receitaMensal,
        BigDecimal investimentoTotal,
        BigDecimal investimentoMensal,
        BigDecimal patrimonio,
        BigDecimal rendimento,
        BigDecimal rendimentoMensal,
        Map<CategoriaRegistro, BigDecimal> gastoCategoriaTotal,
        Map<CategoriaRegistro, BigDecimal> gastoCategoriaMensal,
        Map<CategoriaInvestimento, BigDecimal> investimentoPorCategoria


) {
}
