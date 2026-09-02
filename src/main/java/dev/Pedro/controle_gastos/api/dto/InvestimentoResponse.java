package dev.Pedro.controle_gastos.api.dto;

import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import dev.Pedro.controle_gastos.enums.PeriodicidadeTaxa;


import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoResponse(

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
) {
}
