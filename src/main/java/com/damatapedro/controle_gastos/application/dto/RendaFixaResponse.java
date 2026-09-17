package com.damatapedro.controle_gastos.application.dto;

import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import com.damatapedro.controle_gastos.domain.enumeration.PeriodicidadeTaxa;


import java.math.BigDecimal;
import java.time.LocalDate;

public record RendaFixaResponse(

        Long id,
        String descricao,
        BigDecimal valorAplicado,
        BigDecimal saldoAtual,
        LocalDate data,
        CategoriaInvestimento categoria,
        boolean isAporte,
        boolean isentoIR,
        BigDecimal taxaJuros,
        PeriodicidadeTaxa periodicidadeTaxa,
        LocalDate ultimoSaque,
        BigDecimal rendimento,
        BigDecimal valorBruto
) implements InvestimentoResponse   {
}
