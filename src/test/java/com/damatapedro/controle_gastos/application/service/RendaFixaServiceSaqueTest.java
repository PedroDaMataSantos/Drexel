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

class RendaFixaServiceSaqueTest {

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
