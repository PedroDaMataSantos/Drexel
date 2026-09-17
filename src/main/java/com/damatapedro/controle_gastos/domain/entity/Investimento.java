package com.damatapedro.controle_gastos.domain.entity;

import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investimento")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Investimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "descricao", length = 200)
    private String descricao;

    @Setter
    @NotNull
    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    @Column(name = "valorAplicado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorAplicado;

    @Setter
    @NotNull
    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 20)
    private CategoriaInvestimento categoria;

    @Column(name = "isAporte", nullable = false)
    private boolean isAporte;



    protected Investimento(
            String descricao,
            BigDecimal valorAplicado,
            LocalDate data,
            CategoriaInvestimento categoria,
            Boolean isAporte
    ) {
        this.descricao = descricao;
        this.valorAplicado = valorAplicado;
        this.data = data;
        this.categoria = categoria;
        this.isAporte = isAporte;
    }
}
