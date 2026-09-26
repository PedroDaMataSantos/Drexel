package com.damatapedro.controle_gastos.application.service.crud;

import com.damatapedro.controle_gastos.application.dto.*;
import com.damatapedro.controle_gastos.application.exception.*;
import com.damatapedro.controle_gastos.application.service.*;
import com.damatapedro.controle_gastos.domain.enumeration.*;
import com.damatapedro.controle_gastos.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServicesCenariosTest {
    @Autowired RegistroService registros;
    @Autowired InvestimentoService investimentos;
    @Autowired RendaFixaService rendaFixa;
    @Autowired RegistroRepository registroRepository;
    @Autowired InvestimentoRepository investimentoRepository;
    @Autowired RendaFixaRepository rendaFixaRepository;

    @BeforeEach void limpar() {
        investimentoRepository.deleteAll();
        registroRepository.deleteAll();
    }

    private RendaFixaRequest investimento(BigDecimal valor, CategoriaInvestimento categoria, LocalDate data) {
        return new RendaFixaRequest("CDB teste", valor, data, categoria, new BigDecimal("20"), PeriodicidadeTaxa.ANUAL);
    }
    private RegistroRequest entrada(LocalDate data) {
        return new RegistroRequest(TipoRegistro.ENTRADA, CategoriaRegistro.SALARIO, "Salário teste", new BigDecimal("1000"), data);
    }

    @Test void idsInexistentesNaoAlteramBanco() {
        assertAll(
            () -> assertThrows(RegistroNotFoundException.class, () -> registros.findById(Long.MAX_VALUE)),
            () -> assertThrows(RegistroNotFoundException.class, () -> registros.delete(Long.MAX_VALUE)),
            () -> assertThrows(RegistroNotFoundException.class, () -> registros.update(Long.MAX_VALUE, entrada(null))),
            () -> assertThrows(InvestimentoNotFoundException.class, () -> investimentos.findById(Long.MAX_VALUE)),
            () -> assertThrows(InvestimentoNotFoundException.class, () -> investimentos.delete(Long.MAX_VALUE)),
            () -> assertThrows(InvestimentoNotFoundException.class, () -> rendaFixa.previsaoSaque(Long.MAX_VALUE)),
            () -> assertThrows(InvestimentoNotFoundException.class, () -> rendaFixa.sacar(Long.MAX_VALUE, BigDecimal.ONE))
        );
        assertEquals(0, registroRepository.count());
        assertEquals(0, investimentoRepository.count());
    }

    @Test void consultasSemResultadosRetornamListasVazias() {
        LocalDate hoje = LocalDate.now();
        assertAll(
            () -> assertTrue(registros.findAll().isEmpty()),
            () -> assertTrue(registros.findByCategoria(CategoriaRegistro.SALARIO).isEmpty()),
            () -> assertTrue(registros.findByTipo(TipoRegistro.ENTRADA).isEmpty()),
            () -> assertTrue(registros.findByDescricao("ausente").isEmpty()),
            () -> assertTrue(registros.findByPeriodo(hoje, hoje).isEmpty()),
            () -> assertTrue(investimentos.findAll().isEmpty()),
            () -> assertTrue(investimentos.findByCategoria(CategoriaInvestimento.CDB).isEmpty()),
            () -> assertTrue(investimentos.findByPeriodo(hoje, hoje).isEmpty()),
            () -> assertTrue(investimentos.findByIsAporte(true).isEmpty()),
            () -> assertTrue(investimentos.findByIsAporte(false).isEmpty())
        );
    }

    @Test void filtrosDeInvestimentoSelecionamIdsCorretosIncluindoLimitesDoPeriodo() {
        LocalDate hoje = LocalDate.now();
        var a = rendaFixa.create(investimento(new BigDecimal("100"), CategoriaInvestimento.CDB, hoje), true);
        var b = rendaFixa.create(investimento(new BigDecimal("200"), CategoriaInvestimento.LCI, hoje.minusDays(1)), false);
        assertEquals(a.id(), investimentos.findByCategoria(CategoriaInvestimento.CDB).getFirst().id());
        assertEquals(1, investimentos.findByCategoria(CategoriaInvestimento.CDB).size());
        assertEquals(a.id(), investimentos.findByIsAporte(true).getFirst().id());
        assertEquals(b.id(), investimentos.findByIsAporte(false).getFirst().id());
        assertEquals(1, investimentos.findByPeriodo(hoje, hoje).size());
        assertEquals(a.id(), investimentos.findByPeriodo(hoje, hoje).getFirst().id());
        assertEquals(2, investimentos.findByPeriodo(hoje.minusDays(1), hoje).size());
    }

    @Test void tipoDeInvestimentoNaoSuportadoEhRejeitado() {
        InvestimentoRequest desconhecido = () -> BigDecimal.TEN;
        assertThrows(TipoInvestimentoInvalidoException.class, () -> investimentos.create(desconhecido, false));
        assertEquals(0, investimentoRepository.count());
    }

    @Test void categoriaInvalidaNaoCriaNemAtualizaInvestimento() {
        var original = rendaFixa.create(investimento(new BigDecimal("500"), CategoriaInvestimento.CDB, null), false);
        assertEquals(LocalDate.now(), original.data());
        for (CategoriaInvestimento categoria : new CategoriaInvestimento[]{null, CategoriaInvestimento.ACAO, CategoriaInvestimento.FII}) {
            var invalido = investimento(new BigDecimal("500"), categoria, LocalDate.now());
            assertThrows(CategoriaInvestimentoInvalidoException.class, () -> rendaFixa.create(invalido, false));
            assertThrows(CategoriaInvestimentoInvalidoException.class, () -> rendaFixa.update(original.id(), invalido));
        }
        assertEquals(1, investimentoRepository.count());
        assertEquals(CategoriaInvestimento.CDB, investimentos.findById(original.id()).categoria());
    }

    @Test void atualizarRegistroSemDataPreservaDataOriginal() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        var original = registros.create(entrada(ontem));
        registros.update(original.id(), entrada(null));
        assertEquals(ontem, registros.findById(original.id()).data());
    }

    @Test void categoriaIncompativelNaoAtualizaRegistro() {
        var original = registros.create(entrada(null));
        var invalido = new RegistroRequest(TipoRegistro.SAIDA, CategoriaRegistro.SALARIO, "inválido", BigDecimal.TEN, LocalDate.now());
        assertDoesNotThrow(() -> registros.validarCategoria_Tipo(entrada(null)));
        assertThrows(TipoRegistroInvalidoException.class, () -> registros.validarCategoria_Tipo(invalido));
        assertThrows(TipoRegistroInvalidoException.class, () -> registros.update(original.id(), invalido));
        var persistido = registros.findById(original.id());
        assertEquals(original.id(), persistido.id());
        assertEquals(original.tipoRegistro(), persistido.tipoRegistro());
        assertEquals(original.categoria(), persistido.categoria());
        assertEquals(original.descricao(), persistido.descricao());
        assertEquals(original.data(), persistido.data());
        assertEquals(0, original.valor().compareTo(persistido.valor()));
    }

    @ParameterizedTest @ValueSource(strings = {"0", "-1"})
    void valoresNaoPositivosNaoInvestemNemSacam(String valor) {
        BigDecimal invalido = new BigDecimal(valor);
        assertThrows(ValorInvalidoException.class, () -> registros.investir(investimento(invalido, CategoriaInvestimento.CDB, null)));
        var original = rendaFixa.create(investimento(new BigDecimal("500"), CategoriaInvestimento.CDB, null), false);
        assertThrows(ValorInvalidoException.class, () -> rendaFixa.sacar(original.id(), invalido));
        var persistido = rendaFixaRepository.findById(original.id()).orElseThrow();
        assertEquals(0, new BigDecimal("500").compareTo(persistido.getPrincipalRemanescente()));
        assertEquals(0, new BigDecimal("500").compareTo(persistido.getSaldoAtual()));
        assertNull(persistido.getUltimoSaque());
        assertEquals(0, registroRepository.count());
    }

    @Test void investirExatamenteSaldoDisponivelEhPermitido() {
        registros.create(entrada(null));
        var criado = registros.investir(investimento(new BigDecimal("1000"), CategoriaInvestimento.CDB, null));
        assertTrue(((RendaFixaResponse) criado).isAporte());
    }

    @Test void saqueIntegralZeraPrincipalESaldoEImpedeNovoSaque() {
        var criado = rendaFixa.create(investimento(new BigDecimal("1000"), CategoriaInvestimento.CDB, LocalDate.now().minusDays(365)), false);
        var recebido = rendaFixa.sacar(criado.id(), new BigDecimal("1165.00"));
        var persistido = rendaFixaRepository.findById(criado.id()).orElseThrow();
        assertEquals(0, new BigDecimal("1165").compareTo(recebido.valor()));
        assertEquals(0, BigDecimal.ZERO.compareTo(persistido.getPrincipalRemanescente()));
        assertEquals(0, BigDecimal.ZERO.compareTo(persistido.getSaldoAtual()));
        assertThrows(ValorResgateInsuficienteException.class, () -> rendaFixa.sacar(criado.id(), BigDecimal.ONE));
        assertEquals(1, registroRepository.count());
    }

    @Test void saqueParcialNaoReiniciaIdadeTributariaDoSaldoRestante() {
        var criado = rendaFixa.create(investimento(new BigDecimal("1000"), CategoriaInvestimento.CDB, LocalDate.now().minusDays(365)), false);
        rendaFixa.sacar(criado.id(), new BigDecimal("582.50"));
        var previsao = rendaFixa.previsaoSaque(criado.id());
        // Restam principal 500 + rendimento 100; a aplicação continua tendo 365 dias.
        assertAll(
            () -> assertEquals(new BigDecimal("600.00"), previsao.valorBruto()),
            () -> assertEquals(new BigDecimal("0.00"), previsao.iof()),
            () -> assertEquals(new BigDecimal("17.50"), previsao.ir()),
            () -> assertEquals(new BigDecimal("582.50"), previsao.valorDisponivel())
        );
    }

    @Test void saqueComProporcaoPeriodicaArredondaCentavosSemPerderPrincipal() {
        var criado = rendaFixa.create(investimento(new BigDecimal("300"), CategoriaInvestimento.CDB, null), false);
        rendaFixa.sacar(criado.id(), new BigDecimal("100.00"));
        var persistido = rendaFixaRepository.findById(criado.id()).orElseThrow();
        assertEquals(new BigDecimal("200.00"), persistido.getPrincipalRemanescente());
        assertEquals(new BigDecimal("200.00"), persistido.getSaldoAtual());
        rendaFixa.sacar(criado.id(), new BigDecimal("200.00"));
        assertEquals(new BigDecimal("0.00"), rendaFixaRepository.findById(criado.id()).orElseThrow().getSaldoAtual());
        assertEquals(2, registroRepository.count());
    }
}
