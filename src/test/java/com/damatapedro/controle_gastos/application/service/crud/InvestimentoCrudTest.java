package com.damatapedro.controle_gastos.application.service.crud;

import com.damatapedro.controle_gastos.application.dto.RendaFixaRequest;
import com.damatapedro.controle_gastos.application.dto.RendaFixaResponse;
import com.damatapedro.controle_gastos.application.service.InvestimentoService;
import com.damatapedro.controle_gastos.application.service.RendaFixaService;
import com.damatapedro.controle_gastos.infrastructure.repository.InvestimentoRepository;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import com.damatapedro.controle_gastos.domain.enumeration.PeriodicidadeTaxa;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class InvestimentoCrudTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RendaFixaService rendaFixaService;

    @Autowired
    private InvestimentoService investimentoService;

    @Autowired
    private InvestimentoRepository investimentoRepository;

    @BeforeEach
    void limparInvestimentos() {
        // As requisições HTTP usam outra transação; cada teste começa com o banco limpo.
        investimentoRepository.deleteAll();
    }

    @Test
    void testCreateInvestimentoSuccess() {
        var request = requestValido("teste automatizado");

        var criado = assertInstanceOf(
                RendaFixaResponse.class,
                investimentoService.create(request, false)
        );

        assertNotNull(criado.id());
        assertEquals(request.descricao(), criado.descricao());
        assertEquals(0, request.valorAplicado().compareTo(criado.valorAplicado()));
        assertEquals(0, request.valorAplicado().compareTo(criado.saldoAtual()));
        assertEquals(request.data(), criado.data());
        assertEquals(request.categoria(), criado.categoria());
        assertFalse(criado.isAporte());
        assertFalse(criado.isentoIR());
        assertEquals(0, request.taxaJuros().compareTo(criado.taxaJuros()));
        assertEquals(request.periodicidadeTaxa(), criado.periodicidadeTaxa());
        assertTrue(investimentoRepository.existsById(criado.id()));
    }

    @Test
    void testCreateInvestimentoFailure() {
        var request = new RendaFixaRequest(
                "valor inválido", new BigDecimal("-100.00"), LocalDate.now(),
                CategoriaInvestimento.CDB, new BigDecimal("0.15"),
                PeriodicidadeTaxa.ANUAL);

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> rendaFixaService.create(request, false));

        assertErroDeValidacao(erro, "valorAplicado");
        assertEquals(0L, investimentoRepository.count());
    }

    @Test
    void testCreateAporteDefineIsAporteComoTrue() {
        var criado = assertInstanceOf(
                RendaFixaResponse.class,
                investimentoService.create(requestValido("aporte automatizado"), true)
        );

        assertTrue(criado.isAporte());
        assertTrue(investimentoRepository.existsById(criado.id()));
    }

    @Test
    void testUpdateInvestimentoSuccess() {
        var inicial = requestValido("investimento original");
        var criado = rendaFixaService.create(inicial, false);
        var requestAtualizado = new RendaFixaRequest(
                "descricao atualizada", new BigDecimal("2000.00"), LocalDate.now(),
                CategoriaInvestimento.LCI, new BigDecimal("0.09"),
                PeriodicidadeTaxa.MENSAL);

        rendaFixaService.update(criado.id(), requestAtualizado);

        // A consulta HTTP confirma que a atualização foi persistida.
        var atualizado = buscarPorId(criado.id());
        assertEquals(requestAtualizado.descricao(), atualizado.descricao());
        assertEquals(requestAtualizado.data(), atualizado.data());
        assertEquals(CategoriaInvestimento.LCI, atualizado.categoria());
        assertTrue(atualizado.isentoIR());
        assertEquals(0, requestAtualizado.taxaJuros().compareTo(atualizado.taxaJuros()));
        assertEquals(PeriodicidadeTaxa.MENSAL, atualizado.periodicidadeTaxa());
        // Atualizar os dados não deve substituir o valor originalmente aplicado.
        assertEquals(0, inicial.valorAplicado().compareTo(atualizado.valorAplicado()));
        assertEquals(0, inicial.valorAplicado().compareTo(atualizado.saldoAtual()));
    }

    @Test
    void testUpdateFailsWhenFieldsAreInvalid() {
        var inicial = requestValido("investimento original");
        var criado = rendaFixaService.create(inicial, false);
        var invalido = new RendaFixaRequest(
                "alteração inválida", inicial.valorAplicado(), inicial.data(),
                CategoriaInvestimento.CDB, new BigDecimal("-0.05"),
                PeriodicidadeTaxa.ANUAL);

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> rendaFixaService.update(criado.id(), invalido));

        assertErroDeValidacao(erro, "taxaJuros");
        var persistido = buscarPorId(criado.id());
        assertEquals(inicial.descricao(), persistido.descricao());
        assertEquals(0, inicial.taxaJuros().compareTo(persistido.taxaJuros()));
    }

    @Test
    void testUpdateFailsWhenIdIsInvalid() {
        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> rendaFixaService.update(Long.MAX_VALUE, requestValido("inexistente")));

        assertEquals("Investimento não encontrado", erro.getMessage());
        assertEquals(0L, investimentoRepository.count());
    }

    @Test
    void testGetInvestimentoByIdSuccess() {
        var request = requestValido("investimento para consulta");
        var criado = rendaFixaService.create(request, false);

        var encontrado = buscarPorId(criado.id());

        assertEquals(criado.id(), encontrado.id());
        assertEquals(request.descricao(), encontrado.descricao());
        assertEquals(request.categoria(), encontrado.categoria());
        assertFalse(encontrado.isAporte());
        assertNotNull(encontrado.valorAplicado(), "O valor aplicado deve ser recuperado do banco");
        assertEquals(0, request.valorAplicado().compareTo(encontrado.valorAplicado()));
    }

    @Test
    void testGetInvestimentoAll() {
        var criado1 = rendaFixaService.create(requestValido("primeiro"), false);
        var criado2 = rendaFixaService.create(requestValido("segundo"), false);
        var criado3 = rendaFixaService.create(requestValido("terceiro"), false);

        // Usamos o DTO concreto: InvestimentoResponse agora é uma interface.
        var investimentos = webTestClient.get()
                .uri("/drexel/investimentos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RendaFixaResponse.class)
                .hasSize(3)
                .returnResult().getResponseBody();

        assertNotNull(investimentos);
        var ids = investimentos.stream().map(RendaFixaResponse::id).toList();
        assertTrue(ids.contains(criado1.id()));
        assertTrue(ids.contains(criado2.id()));
        assertTrue(ids.contains(criado3.id()));
    }

    @Test
    void testDeleteInvestimentoSuccess() {
        var criado = rendaFixaService.create(requestValido("investimento para excluir"), false);

        webTestClient.delete()
                .uri("/drexel/investimentos/{id}", criado.id())
                .exchange()
                .expectStatus().isNoContent();

        assertFalse(investimentoRepository.existsById(criado.id()));
        webTestClient.get().uri("/drexel/investimentos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RendaFixaResponse.class).hasSize(0);
    }

    private RendaFixaRequest requestValido(String descricao) {
        return new RendaFixaRequest(
                descricao, new BigDecimal("500.00"), LocalDate.now(),
                CategoriaInvestimento.CDB, new BigDecimal("0.15"),
                PeriodicidadeTaxa.ANUAL);
    }

    private RendaFixaResponse buscarPorId(Long id) {
        var response = webTestClient.get()
                .uri("/drexel/investimentos/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RendaFixaResponse.class)
                .returnResult().getResponseBody();

        assertNotNull(response);
        return response;
    }

    private void assertErroDeValidacao(Throwable erro, String campo) {
        // A validação pode vir encapsulada pelo fechamento da transação.
        while (erro.getCause() != null) {
            erro = erro.getCause();
        }
        var validacao = assertInstanceOf(ConstraintViolationException.class, erro);
        assertTrue(validacao.getConstraintViolations().stream()
                .anyMatch(violacao -> violacao.getPropertyPath().toString().equals(campo)),
                "A validação deve rejeitar o campo " + campo);
    }
}

