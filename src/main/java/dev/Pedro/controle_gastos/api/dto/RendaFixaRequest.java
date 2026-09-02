package dev.Pedro.controle_gastos.api.dto;

import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import dev.Pedro.controle_gastos.enums.PeriodicidadeTaxa;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RendaFixaRequest(

        String descricao,
        BigDecimal valorAplicado,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate data,
        CategoriaInvestimento categoria,
        boolean isAporte,
        BigDecimal taxaJuros,
        PeriodicidadeTaxa periodicidadeTaxa
) {
}
