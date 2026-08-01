package dev.Pedro.controle_gastos.api.service.calculo;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RendimentoCalculatorTest {

    @Test
    void calcularDiasCorridos_deveContarDiasEntreDuasDatas() {

        // Arrange: preparo os dados de entrada
        LocalDate aplicacao = LocalDate.of(2026, 1, 1);
        LocalDate saque = LocalDate.of(2026, 1, 11);

        // Act: chamo o método que quero testar
        Long resultado = RendimentoCalculator.calcularDiasCorridos(aplicacao, saque);

        // Assert: confiro se o resultado é o esperado
        assertThat(resultado).isEqualTo(10L);
    }



    @Test
    void calcularDiasCorridos_deveRetornarZero_quandoDatasSaoIguais() {

        LocalDate mesmaData = LocalDate.of(2026, 3, 5);

        Long resultado = RendimentoCalculator.calcularDiasCorridos(mesmaData, mesmaData);

        assertThat(resultado).isEqualTo(0L);
    }
}
