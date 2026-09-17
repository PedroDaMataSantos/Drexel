package com.damatapedro.controle_gastos.application.dto;

import com.damatapedro.controle_gastos.domain.enumeration.CategoriaRegistro;
import com.damatapedro.controle_gastos.domain.enumeration.TipoRegistro;

import java.math.BigDecimal;
import java.time.LocalDate;


public record RegistroResponse(
        Long id,
        TipoRegistro tipoRegistro,
        CategoriaRegistro categoria,
        String descricao,
        BigDecimal valor,
        LocalDate data

) {
}
