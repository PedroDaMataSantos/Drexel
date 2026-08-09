package dev.Pedro.controle_gastos.domain.entity;

import dev.Pedro.controle_gastos.enums.CategoriaRegistro;
import dev.Pedro.controle_gastos.enums.TipoRegistro;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "registro")
@Getter
@Setter
@NoArgsConstructor

public class Registro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro", nullable = false, length = 20)
    private TipoRegistro tipoRegistro;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 20)
    private CategoriaRegistro categoria;


    @Size(max = 200)
    @Column(name = "descricao", nullable = true, length = 200)
    private String descricao;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @NotNull
    @Column(name = "data", nullable = false)
    private LocalDate data;


    public Registro(TipoRegistro tipoRegistro, CategoriaRegistro categoria, String descricao, BigDecimal valor, LocalDate data) {
        this.tipoRegistro = tipoRegistro;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
    }
}





