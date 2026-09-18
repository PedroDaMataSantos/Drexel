package com.damatapedro.controle_gastos.application.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RendaFixaRequest.class, name = "RENDA_FIXA")
})
public interface InvestimentoRequest {
    BigDecimal valorAplicado();
}
