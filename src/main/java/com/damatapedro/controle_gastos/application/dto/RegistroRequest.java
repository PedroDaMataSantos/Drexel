package com.damatapedro.controle_gastos.application.dto;

import com.damatapedro.controle_gastos.domain.enumeration.CategoriaRegistro;
import com.damatapedro.controle_gastos.domain.enumeration.TipoRegistro;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;


public record RegistroRequest(

        TipoRegistro tipoRegistro,
        CategoriaRegistro categoria,
        String descricao,
        BigDecimal valor,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate data

) {
}
