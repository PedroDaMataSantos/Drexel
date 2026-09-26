package com.damatapedro.controle_gastos.application.service.crud;

import com.damatapedro.controle_gastos.application.dto.*;
import com.damatapedro.controle_gastos.application.service.*;
import com.damatapedro.controle_gastos.domain.enumeration.*;
import com.damatapedro.controle_gastos.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DashboardServiceTest {
    @Autowired DashboardService dashboard;
    @Autowired RegistroService registros;
    @Autowired RendaFixaService rendaFixa;
    @Autowired RegistroRepository registroRepository;
    @Autowired InvestimentoRepository investimentoRepository;

    @BeforeEach void limpar() {
        investimentoRepository.deleteAll();
        registroRepository.deleteAll();
    }

    private void dinheiro(String esperado, BigDecimal recebido) {
        assertEquals(new BigDecimal(esperado), recebido.setScale(2, RoundingMode.HALF_EVEN));
    }
    private void registro(TipoRegistro tipo, CategoriaRegistro categoria, String valor, LocalDate data) {
        registros.create(new RegistroRequest(tipo, categoria, "Teste dashboard", new BigDecimal(valor), data));
    }
    private void investimento(String valor, LocalDate data, boolean aporte, CategoriaInvestimento categoria, String taxa) {
        rendaFixa.create(new RendaFixaRequest("Teste dashboard", new BigDecimal(valor), data, categoria, new BigDecimal(taxa), PeriodicidadeTaxa.ANUAL), aporte);
    }
    private void categorias(Map<CategoriaRegistro, BigDecimal> mapa, String alimentacao, String lazer) {
        assertEquals(Arrays.stream(CategoriaRegistro.values()).filter(c -> c.getTipo() == TipoRegistro.SAIDA).count(), mapa.size());
        for (var categoria : CategoriaRegistro.values()) {
            if (categoria.getTipo() == TipoRegistro.ENTRADA) assertFalse(mapa.containsKey(categoria));
            else dinheiro(categoria == CategoriaRegistro.ALIMENTACAO ? alimentacao : categoria == CategoriaRegistro.LAZER ? lazer : "0.00", mapa.get(categoria));
        }
    }

    @Test void todosOsIndicadoresSemDadosSaoZero() {
        assertAll(
            () -> dinheiro("0.00", dashboard.totalInvestido()),
            () -> dinheiro("0.00", dashboard.totalEntrada()),
            () -> dinheiro("0.00", dashboard.totalSaida()),
            () -> dinheiro("0.00", dashboard.saldoTotal()),
            () -> dinheiro("0.00", dashboard.aporte()),
            () -> dinheiro("0.00", dashboard.patrimonio()),
            () -> dinheiro("0.00", dashboard.rendimentoTotal()),
            () -> dinheiro("0.00", dashboard.entradaMensal()),
            () -> dinheiro("0.00", dashboard.saidaMensal()),
            () -> dinheiro("0.00", dashboard.investimentoMensal()),
            () -> dinheiro("0.00", dashboard.saldoMensal()),
            () -> dinheiro("0.00", dashboard.aporteMensal()),
            () -> dinheiro("0.00", dashboard.rendimentoMensal())
        );
        categorias(dashboard.gastoCategoriaTotal(), "0.00", "0.00");
        categorias(dashboard.gastoCategoriaMensal(), "0.00", "0.00");
        assertEquals(CategoriaInvestimento.values().length, dashboard.somaInvestimentoCategoriaTotal().size());
        dashboard.somaInvestimentoCategoriaTotal().values().forEach(v -> dinheiro("0.00", v));
        var resposta = dashboard.dashboard();
        dinheiro("0.00", resposta.saldoTotal());
        dinheiro("0.00", resposta.rendimento());
        dinheiro("0.00", resposta.patrimonio());
    }

    @Test void indicadoresSeparamMesAtualHistoricoAportesECategorias() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate anterior = inicio.minusDays(1);
        registro(TipoRegistro.ENTRADA, CategoriaRegistro.SALARIO, "1000", inicio);
        registro(TipoRegistro.ENTRADA, CategoriaRegistro.OUTRAS, "500", anterior);
        registro(TipoRegistro.SAIDA, CategoriaRegistro.ALIMENTACAO, "100", hoje);
        registro(TipoRegistro.SAIDA, CategoriaRegistro.LAZER, "50", anterior);
        investimento("200", hoje, true, CategoriaInvestimento.CDB, "0");
        investimento("300", hoje, false, CategoriaInvestimento.LCI, "0");
        investimento("100", anterior, true, CategoriaInvestimento.CDB, "0");
        assertAll(
            () -> dinheiro("600.00", dashboard.totalInvestido()),
            () -> dinheiro("1500.00", dashboard.totalEntrada()),
            () -> dinheiro("150.00", dashboard.totalSaida()),
            () -> dinheiro("1050.00", dashboard.saldoTotal()),
            () -> dinheiro("300.00", dashboard.aporte()),
            () -> dinheiro("1650.00", dashboard.patrimonio()),
            () -> dinheiro("0.00", dashboard.rendimentoTotal()),
            () -> dinheiro("1000.00", dashboard.entradaMensal()),
            () -> dinheiro("100.00", dashboard.saidaMensal()),
            () -> dinheiro("500.00", dashboard.investimentoMensal()),
            () -> dinheiro("700.00", dashboard.saldoMensal()),
            () -> dinheiro("200.00", dashboard.aporteMensal()),
            () -> dinheiro("0.00", dashboard.rendimentoMensal())
        );
        categorias(dashboard.gastoCategoriaTotal(), "100.00", "50.00");
        categorias(dashboard.gastoCategoriaMensal(), "100.00", "0.00");
        var porCategoria = dashboard.somaInvestimentoCategoriaTotal();
        for (var categoria : CategoriaInvestimento.values()) {
            dinheiro(categoria == CategoriaInvestimento.CDB || categoria == CategoriaInvestimento.LCI ? "300.00" : "0.00", porCategoria.get(categoria));
        }
        var resposta = dashboard.dashboard();
        assertAll(
            () -> dinheiro("1050.00", resposta.saldoTotal()),
            () -> dinheiro("700.00", resposta.saldoMensal()),
            () -> dinheiro("150.00", resposta.gastosTotal()),
            () -> dinheiro("100.00", resposta.gastosMensal()),
            () -> dinheiro("1500.00", resposta.receitaTotal()),
            () -> dinheiro("1000.00", resposta.receitaMensal()),
            () -> dinheiro("600.00", resposta.investimentoTotal()),
            () -> dinheiro("500.00", resposta.investimentoMensal()),
            () -> dinheiro("1650.00", resposta.patrimonio()),
            () -> dinheiro("0.00", resposta.rendimento()),
            () -> dinheiro("0.00", resposta.rendimentoMensal()),
            () -> assertEquals(dashboard.gastoCategoriaTotal(), resposta.gastoCategoriaTotal()),
            () -> assertEquals(dashboard.gastoCategoriaMensal(), resposta.gastoCategoriaMensal()),
            () -> assertEquals(porCategoria, resposta.investimentoPorCategoria())
        );
    }

    @Test void rendimentoTotalSomaMaisDeUmaAplicacao() {
        investimento("1000", LocalDate.now().minusDays(365), false, CategoriaInvestimento.CDB, "20");
        investimento("500", LocalDate.now().minusDays(365), false, CategoriaInvestimento.LCI, "10");
        dinheiro("250.00", dashboard.rendimentoTotal());
        // Caracteriza o filtro atual: aplicações criadas fora do mês não entram nesta soma.
        dinheiro("0.00", dashboard.rendimentoMensal());
    }
}
