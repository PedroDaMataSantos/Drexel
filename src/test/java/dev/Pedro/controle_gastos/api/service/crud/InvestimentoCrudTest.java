package dev.Pedro.controle_gastos.api.service.crud;

import dev.Pedro.controle_gastos.api.dto.InvestimentoRequest;
import dev.Pedro.controle_gastos.api.dto.InvestimentoResponse;
import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import dev.Pedro.controle_gastos.enums.PeriodicidadeTaxa;
import dev.Pedro.controle_gastos.enums.TipoInvestimento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

class InvestimentoCrudTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCreateInvestimentoSucess() {
        var request = new InvestimentoRequest(
                "teste automatizado",
                new BigDecimal(1000),
                LocalDate.of(2026, 1, 1),
                CategoriaInvestimento.CDB,
                TipoInvestimento.INVESTIMENTO,
                new BigDecimal("0.105"),
                PeriodicidadeTaxa.ANUAL);

        webTestClient
                .post()
                .uri("/scontg/investimentos")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.descricao").isEqualTo(request.descricao())
                .jsonPath("$.valorAplicado").isEqualTo(request.valorAplicado())
                .jsonPath("$.data").isEqualTo(request.data().toString())
                .jsonPath("$.categoria").isEqualTo(request.categoria().toString())
                .jsonPath("$.tipo").isEqualTo(request.tipo().toString())
                .jsonPath("$.taxaJuros").isEqualTo(request.taxaJuros())
                .jsonPath("$.periodicidadeTaxa").isEqualTo(request.periodicidadeTaxa().toString())
                .jsonPath("$.isentoIR").isEqualTo(false);

    }

    @Test
    void testCreateInvestimentoFailure() {
        var request = new InvestimentoRequest(
                "teste automatizado",
                null,
                null,
                null,
                null,
                null,
                null);

        webTestClient
                .post()
                .uri("/scontg/investimentos")
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateInvestimentoSucess() {
        var request = new InvestimentoRequest(
                "",
                new BigDecimal(100),
                LocalDate.now(),
                CategoriaInvestimento.CDB,
                TipoInvestimento.INVESTIMENTO,
                new BigDecimal("0.105"),
                PeriodicidadeTaxa.ANUAL
        );

        InvestimentoResponse criado = webTestClient
                .post()
                .uri("/scontg/investimentos")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvestimentoResponse.class)
                .returnResult()
                .getResponseBody();


        var requestAtualizado = new InvestimentoRequest(
                "descricao atualizada",
                new BigDecimal(2000),
                LocalDate.of(2026, 6, 1),
                CategoriaInvestimento.LCI,
                TipoInvestimento.INVESTIMENTO,
                new BigDecimal("0.09"),
                PeriodicidadeTaxa.MENSAL);

        webTestClient
                .put()
                .uri("/scontg/investimentos/" + criado.id())
                .bodyValue(requestAtualizado)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.descricao").isEqualTo(requestAtualizado.descricao())
                .jsonPath("$.categoria").isEqualTo(requestAtualizado.categoria().toString())
                .jsonPath("$.isentoIR").isEqualTo(true);
    }

    @Test
    void testUpdateFailsWhenFieldsAreInvalid() {
        var requestInicial = new InvestimentoRequest(
                "investimento original",
                new BigDecimal("500.00"),
                LocalDate.of(2026, 1, 1),
                CategoriaInvestimento.CDB,
                TipoInvestimento.INVESTIMENTO,
                new BigDecimal("0.15"),
                PeriodicidadeTaxa.ANUAL
        );

        InvestimentoResponse criado = webTestClient
                .post()
                .uri("/scontg/investimentos")
                .bodyValue(requestInicial)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvestimentoResponse.class)
                .returnResult()
                .getResponseBody();

        var requestInvalido = new InvestimentoRequest(
                null,
                new BigDecimal("-100.00"),
                null,
                null,
                null,
                new BigDecimal("-0.05"),
                null
        );

        webTestClient
                .put()
                .uri("/scontg/investimentos/{id}", criado.id())
                .bodyValue(requestInvalido)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateFailsWhenIdIsInvalid() {
        Long id = Long.MAX_VALUE;

        var request = new InvestimentoRequest(
                "investimento original",
                new BigDecimal("500.00"),
                LocalDate.of(2026, 1, 1),
                CategoriaInvestimento.CDB,
                TipoInvestimento.INVESTIMENTO,
                new BigDecimal("0.15"),
                PeriodicidadeTaxa.ANUAL
        );

        webTestClient
                .put()
                .uri("/scontg/investimentos/{id}", id)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testGetInvestimentoByIdSuccess() {
        var request = new InvestimentoRequest(
                "investimento para consulta",
                new BigDecimal("500.00"),
                LocalDate.of(2026, 1, 1),
                CategoriaInvestimento.CDB,
                TipoInvestimento.INVESTIMENTO,
                new BigDecimal("0.15"),
                PeriodicidadeTaxa.ANUAL
        );

        InvestimentoResponse criado = webTestClient
                .post()
                .uri("/scontg/investimentos")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvestimentoResponse.class)
                .returnResult()
                .getResponseBody();

        webTestClient
                .get()
                .uri("/scontg/investimentos/{id}", criado.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(criado.id().intValue())
                .jsonPath("$.descricao").isEqualTo(request.descricao())
                .jsonPath("$.categoria").isEqualTo(request.categoria().toString());
    }

    @Test
    void testGetInvestimentoAll() {
        var request1 = new InvestimentoRequest(
                "investimento para consulta",
                new BigDecimal("500.00"),
                LocalDate.of(2026, 1, 1),
                CategoriaInvestimento.CDB,
                TipoInvestimento.INVESTIMENTO,
                new BigDecimal("0.15"),
                PeriodicidadeTaxa.ANUAL
        );

        var request2 = new InvestimentoRequest(
                "investimento original",
                new BigDecimal("500.00"),
                LocalDate.of(2026, 1, 1),
                CategoriaInvestimento.CDB,
                TipoInvestimento.INVESTIMENTO,
                new BigDecimal("0.15"),
                PeriodicidadeTaxa.ANUAL
        );
        var request3 = new InvestimentoRequest(
                "teste automatizado",
                new BigDecimal(1000),
                LocalDate.of(2026, 1, 1),
                CategoriaInvestimento.CDB,
                TipoInvestimento.INVESTIMENTO,
                new BigDecimal("0.105"),
                PeriodicidadeTaxa.ANUAL
        );

        InvestimentoResponse criado1 = webTestClient
                .post()
                .uri("/scontg/investimentos")
                .bodyValue(request1)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvestimentoResponse.class)
                .returnResult()
                .getResponseBody();

        InvestimentoResponse criado2 = webTestClient
                .post()
                .uri("/scontg/investimentos")
                .bodyValue(request2)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvestimentoResponse.class)
                .returnResult()
                .getResponseBody();

        InvestimentoResponse criado3 = webTestClient
                .post()
                .uri("/scontg/investimentos")
                .bodyValue(request3)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvestimentoResponse.class)
                .returnResult()
                .getResponseBody();


        List<InvestimentoResponse> investimentos = webTestClient
                .get()
                .uri("/scontg/investimentos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InvestimentoResponse.class)
                .returnResult()
                .getResponseBody();

        List<Long> ids = investimentos.stream()
                .map(InvestimentoResponse::id)
                .toList();

        assertTrue(ids.containsAll(List.of(criado1.id(), criado2.id(), criado3.id())));


    }

    


}

