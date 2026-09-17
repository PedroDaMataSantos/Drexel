package dev.Pedro.controle_gastos.api.dto;

import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
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
