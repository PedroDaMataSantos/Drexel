package com.damatapedro.controle_gastos.application.dto;

import java.math.BigDecimal;

public record PrevisaoSaqueResponse(
        BigDecimal valorBruto,
        BigDecimal iof,
        BigDecimal ir,
        BigDecimal valorDisponivel
) {}
