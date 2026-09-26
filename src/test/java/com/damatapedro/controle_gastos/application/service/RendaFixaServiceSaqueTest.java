package com.damatapedro.controle_gastos.application.service;

import com.damatapedro.controle_gastos.application.mapper.InvestimentoMapper;
import com.damatapedro.controle_gastos.domain.entity.Registro;
import com.damatapedro.controle_gastos.domain.entity.RendaFixa;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import com.damatapedro.controle_gastos.domain.enumeration.PeriodicidadeTaxa;
import com.damatapedro.controle_gastos.infrastructure.repository.RegistroRepository;
import com.damatapedro.controle_gastos.infrastructure.repository.RendaFixaRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.CALLS_REAL_METHODS;

class RendaFixaServiceSaqueTest {

    @Test
    void deveSimularAplicacaoEmJaneiroSaqueEmSetembroENovosJurosEmOutubro() {
        LocalDate aplicacao = LocalDate.of(2026, 1, 1);
        LocalDate primeiroSaque = LocalDate.of(2026, 9, 25);
        LocalDate consultaSeguinte = LocalDate.of(2026, 10, 25);
        var repository = mock(RendaFixaRepository.class);
        var registros = mock(RegistroRepository.class);
        var service = new RendaFixaService(repository, registros, mock(InvestimentoMapper.class));
        var investimento = new RendaFixa("CDB janeiro", new BigDecimal("1000.00"),
                aplicacao, CategoriaInvestimento.CDB, false, false,
                new BigDecimal("20.00"), PeriodicidadeTaxa.ANUAL);
        when(repository.findById(1L)).thenReturn(Optional.of(investimento));
        when(repository.save(any(RendaFixa.class))).thenAnswer(i -> i.getArgument(0));
        when(registros.save(any(Registro.class))).thenAnswer(i -> i.getArgument(0));

        // Congela apenas o relógio; service e cálculos são reais.
        try (var datas = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            datas.when(LocalDate::now).thenReturn(primeiroSaque);
            var antes = service.previsaoSaque(1L);
            // Oráculo independente: 1000 * 1,20^(267/365); IR de 20% sobre o lucro.
            assertAll(
                    () -> assertEquals(new BigDecimal("1142.67"), antes.valorBruto()),
                    () -> assertEquals(new BigDecimal("0.00"), antes.iof()),
                    () -> assertEquals(new BigDecimal("28.53"), antes.ir()),
                    () -> assertEquals(new BigDecimal("1114.14"), antes.valorDisponivel())
            );
            var recebido = service.sacar(1L, new BigDecimal("557.07"));
            var depois = service.previsaoSaque(1L);
            assertAll(
                    () -> assertEquals(new BigDecimal("557.07"), recebido.valor()),
                    () -> assertEquals(primeiroSaque, recebido.data()),
                    () -> assertEquals(aplicacao, investimento.getData()),
                    () -> assertEquals(primeiroSaque, investimento.getUltimoSaque()),
                    () -> assertEquals(new BigDecimal("500.00"), investimento.getPrincipalRemanescente()),
                    () -> assertEquals(new BigDecimal("571.34"), investimento.getSaldoAtual()),
                    () -> assertEquals(new BigDecimal("571.34"), depois.valorBruto()),
                    () -> assertEquals(new BigDecimal("0.00"), depois.iof()),
                    () -> assertEquals(new BigDecimal("14.27"), depois.ir()),
                    () -> assertEquals(new BigDecimal("557.07"), depois.valorDisponivel())
            );
            datas.when(LocalDate::now).thenReturn(consultaSeguinte);
            var outubro = service.previsaoSaque(1L);
            // Os juros seguintes partem de 571,34 por apenas 30 dias, não desde janeiro.
            assertAll(
                    () -> assertEquals(new BigDecimal("579.97"), outubro.valorBruto()),
                    () -> assertEquals(new BigDecimal("0.00"), outubro.iof()),
                    () -> assertEquals(new BigDecimal("15.99"), outubro.ir()),
                    () -> assertEquals(new BigDecimal("563.98"), outubro.valorDisponivel())
            );
        }
    }

    @Test
    void deveManterMetadeDoPrincipalEDoSaldoBrutoAoResgatarMetadeDoLiquido() {
        RendaFixaRepository rendaFixaRepository = mock(RendaFixaRepository.class);
        RegistroRepository registroRepository = mock(RegistroRepository.class);
        InvestimentoMapper mapper = mock(InvestimentoMapper.class);

        RendaFixaService service = new RendaFixaService(
                rendaFixaRepository,
                registroRepository,
                mapper
        );

        RendaFixa rendaFixa = new RendaFixa(
                "CDB com rendimento",
                new BigDecimal("1000.00"),
                LocalDate.now().minusDays(365),
                CategoriaInvestimento.CDB,
                false,
                false,
                new BigDecimal("20.00"),
                PeriodicidadeTaxa.ANUAL
        );

        when(rendaFixaRepository.findById(1L)).thenReturn(Optional.of(rendaFixa));
        when(rendaFixaRepository.save(any(RendaFixa.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));
        when(registroRepository.save(any(Registro.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        BigDecimal metadeDoValorLiquido = new BigDecimal("582.50");

        var registro = service.sacar(1L, metadeDoValorLiquido);

        assertAll(
                () -> assertEquals(new BigDecimal("582.50"), registro.valor()),
                () -> assertEquals(new BigDecimal("500.00"), rendaFixa.getPrincipalRemanescente()),
                () -> assertEquals(new BigDecimal("600.00"), rendaFixa.getSaldoAtual())
        );
    }
}
