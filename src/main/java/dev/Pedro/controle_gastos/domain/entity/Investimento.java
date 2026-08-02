package dev.Pedro.controle_gastos.domain.entity;

import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import dev.Pedro.controle_gastos.enums.PeriodicidadeTaxa;
import dev.Pedro.controle_gastos.enums.TipoInvestimento;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investimento")
@Getter
@Setter
@NoArgsConstructor

public class Investimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", nullable = true, length = 200)
    private String descricao;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Column(name = "valorAplicado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorAplicado;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Column(name = "valorPosSaque", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPosSaque;


    @NotNull
    @Column(name = "data", nullable = false)
    private LocalDate data;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 20)
    private CategoriaInvestimento categoria;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoInvestimento tipo;

    @Column(name = "isentoIR" , nullable = false)
    private boolean isentoIR;

    @NotNull
    @Column(name = "taxa_juros",nullable = false, precision = 8, scale = 4)
    private BigDecimal taxaJuros;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodicidade_taxa", length = 10)
    private PeriodicidadeTaxa periodicidadeTaxa;

    @Column(name = "ultimo_saque")
    private LocalDate ultimoSaque;


    public Investimento(String descricao, BigDecimal valorAplicado,
                        LocalDate data, CategoriaInvestimento categoria,
                        TipoInvestimento tipo, boolean isentoIR,
                        BigDecimal taxaJuros, PeriodicidadeTaxa periodicidadeTaxa) {

        this.descricao = descricao;
        this.valorAplicado = valorAplicado;
        this.valorPosSaque = valorAplicado;
        this.data = data;
        this.categoria = categoria;
        this.tipo = tipo;
        this.isentoIR = isentoIR;
        this.taxaJuros = taxaJuros;
        this.periodicidadeTaxa = periodicidadeTaxa;
    }
}
