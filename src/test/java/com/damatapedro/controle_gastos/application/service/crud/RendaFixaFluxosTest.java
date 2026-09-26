package com.damatapedro.controle_gastos.application.service.crud;

import com.damatapedro.controle_gastos.application.dto.RegistroRequest;
import com.damatapedro.controle_gastos.application.dto.RendaFixaRequest;
import com.damatapedro.controle_gastos.application.dto.RendaFixaResponse;
import com.damatapedro.controle_gastos.application.exception.SaldoInsuficienteException;
import com.damatapedro.controle_gastos.application.exception.ValorResgateInsuficienteException;
import com.damatapedro.controle_gastos.application.service.DashboardService;
import com.damatapedro.controle_gastos.application.service.RegistroService;
import com.damatapedro.controle_gastos.application.service.RendaFixaService;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaRegistro;
import com.damatapedro.controle_gastos.domain.enumeration.PeriodicidadeTaxa;
import com.damatapedro.controle_gastos.domain.enumeration.TipoRegistro;
import com.damatapedro.controle_gastos.infrastructure.repository.InvestimentoRepository;
import com.damatapedro.controle_gastos.infrastructure.repository.RegistroRepository;
import com.damatapedro.controle_gastos.infrastructure.repository.RendaFixaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RendaFixaFluxosTest {

    @Autowired
    private RendaFixaService rendaFixaService;

    @Autowired
    private RegistroService registroService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private RendaFixaRepository rendaFixaRepository;

    @Autowired
    private InvestimentoRepository investimentoRepository;

    @Autowired
    private RegistroRepository registroRepository;

    @BeforeEach
    void limparBanco() {
        investimentoRepository.deleteAll();
        registroRepository.deleteAll();
    }

    @Test
    void deveInvestirSaldoComoAporte() {
        registroService.create(new RegistroRequest(
                TipoRegistro.ENTRADA,
                CategoriaRegistro.SALARIO,
                "Salário",
                new BigDecimal("1000.00"),
                LocalDate.now()
        ));

        var response = (RendaFixaResponse) registroService.investir(
                rendaFixaRequest("Aporte em CDB", new BigDecimal("400.00"), LocalDate.now())
        );

        assertTrue(response.isAporte());
        assertEquals(0, new BigDecimal("400.00").compareTo(response.valorAplicado()));
        assertEquals(0, new BigDecimal("400.00").compareTo(dashboardService.aporte()));
        assertEquals(0, new BigDecimal("600.00").compareTo(dashboardService.saldoTotal()));
        assertTrue(investimentoRepository.existsById(response.id()));
        assertEquals(1, registroRepository.count());
    }

    @Test
    void deveRejeitarInvestimentoMaiorQueSaldo() {
        registroService.create(new RegistroRequest(
                TipoRegistro.ENTRADA,
                CategoriaRegistro.SALARIO,
                "Salário",
                new BigDecimal("300.00"),
                LocalDate.now()
        ));

        var erro = assertThrows(SaldoInsuficienteException.class, () -> registroService.investir(
                rendaFixaRequest("Aporte acima do saldo", new BigDecimal("400.00"), LocalDate.now())
        ));

        assertEquals("Saldo insuficiente", erro.getMessage());
        assertEquals(0, investimentoRepository.count());
    }

    @Test
    void deveCalcularPrevisaoDeSaque() {
        var criado = rendaFixaService.create(
                rendaFixaRequest("CDB antigo", new BigDecimal("500.00"), LocalDate.now().minusDays(365)),
                false
        );

        var previsao = rendaFixaService.previsaoSaque(criado.id());

        assertTrue(previsao.valorBruto().compareTo(new BigDecimal("500.00")) > 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(previsao.iof()));
        assertTrue(previsao.ir().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(previsao.valorDisponivel().compareTo(new BigDecimal("500.00")) > 0);
        assertTrue(previsao.valorDisponivel().compareTo(previsao.valorBruto()) < 0);
    }

    @Test
    void deveSacarERegistrarEntradaNoFluxoDeCaixa() {
        var criado = rendaFixaService.create(
                rendaFixaRequest("CDB para saque", new BigDecimal("500.00"), LocalDate.now()),
                false
        );

        var registroSaque = rendaFixaService.sacar(criado.id(), new BigDecimal("200.00"));
        var rendaFixaPersistida = rendaFixaRepository.findById(criado.id()).orElseThrow();

        assertEquals(TipoRegistro.ENTRADA, registroSaque.tipoRegistro());
        assertEquals(CategoriaRegistro.INVESTIMENTO, registroSaque.categoria());
        assertEquals(0, new BigDecimal("200.00").compareTo(registroSaque.valor()));
        assertEquals(LocalDate.now(), registroSaque.data());
        assertEquals(0, new BigDecimal("300.00").compareTo(rendaFixaPersistida.getSaldoAtual()));
        assertEquals(LocalDate.now(), rendaFixaPersistida.getUltimoSaque());
        assertTrue(registroRepository.existsById(registroSaque.id()));
    }

    @Test
    void devePreservarPrincipalERendimentoNaoResgatadosNoSaqueParcial() {
        var criado = rendaFixaService.create(
                new RendaFixaRequest(
                        "CDB com rendimento",
                        new BigDecimal("1000.00"),
                        LocalDate.now().minusDays(365),
                        CategoriaInvestimento.CDB,
                        new BigDecimal("20.00"),
                        PeriodicidadeTaxa.ANUAL
                ),
                false
        );

        var previsaoAntesDoSaque = rendaFixaService.previsaoSaque(criado.id());
        BigDecimal valorSolicitado = previsaoAntesDoSaque.valorDisponivel()
                .divide(new BigDecimal("2.00"));

        rendaFixaService.sacar(criado.id(), valorSolicitado);

        var rendaFixaPersistida = rendaFixaRepository.findById(criado.id()).orElseThrow();
        BigDecimal principalEsperado = new BigDecimal("500.00");
        BigDecimal saldoBrutoEsperado = previsaoAntesDoSaque.valorBruto()
                .divide(new BigDecimal("2.00"));

        assertEquals(0, principalEsperado.compareTo(rendaFixaPersistida.getPrincipalRemanescente()));
        assertEquals(0, saldoBrutoEsperado.compareTo(rendaFixaPersistida.getSaldoAtual()));
    }

    @Test
    void deveRejeitarSaqueMaiorQueValorDisponivel() {
        var criado = rendaFixaService.create(
                rendaFixaRequest("CDB para saque inválido", new BigDecimal("500.00"), LocalDate.now()),
                false
        );

        var erro = assertThrows(
                ValorResgateInsuficienteException.class,
                () -> rendaFixaService.sacar(criado.id(), new BigDecimal("600.00"))
        );
        var rendaFixaPersistida = rendaFixaRepository.findById(criado.id()).orElseThrow();

        assertEquals(
                "O valor disponivel para saque é menor que o valor digitado. Disponivel:500.00",
                erro.getMessage()
        );
        assertEquals(0, new BigDecimal("500.00").compareTo(rendaFixaPersistida.getSaldoAtual()));
        assertEquals(0, registroRepository.count());
    }

    private RendaFixaRequest rendaFixaRequest(String descricao, BigDecimal valor, LocalDate data) {
        return new RendaFixaRequest(
                descricao,
                valor,
                data,
                CategoriaInvestimento.CDB,
                new BigDecimal("15.00"),
                PeriodicidadeTaxa.ANUAL
        );
    }
}
