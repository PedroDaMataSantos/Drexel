package dev.Pedro.controle_gastos.domain.entity;

import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import dev.Pedro.controle_gastos.enums.PeriodicidadeTaxa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "renda_fixa")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RendaFixa extends Investimento {

    @NotNull
    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    @Column(name = "saldo_atual", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoAtual;

    @Column(name = "isento_ir", nullable = false)
    private boolean isentoIR;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 4, fraction = 4)
    @Column(name = "taxa_juros", nullable = false, precision = 8, scale = 4)
    private BigDecimal taxaJuros;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "periodicidade_taxa", nullable = false, length = 10)
    private PeriodicidadeTaxa periodicidadeTaxa;

    @Column(name = "ultimo_saque")
    private LocalDate ultimoSaque;

    public RendaFixa(
            String descricao,
            BigDecimal valorAplicado,
            LocalDate data,
            CategoriaInvestimento categoria,
            boolean isAporte,
            boolean isentoIR,
            BigDecimal taxaJuros,
            PeriodicidadeTaxa periodicidadeTaxa
    ) {
        super(
                descricao,
                valorAplicado,
                data,
                categoria,
                isAporte
        );

        this.saldoAtual = valorAplicado;
        this.isentoIR = isentoIR;
        this.taxaJuros = taxaJuros;
        this.periodicidadeTaxa = periodicidadeTaxa;

    }
}
