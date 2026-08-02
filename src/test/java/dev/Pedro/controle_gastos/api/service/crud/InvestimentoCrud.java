package dev.Pedro.controle_gastos.api.service.crud;

import dev.Pedro.controle_gastos.api.dto.InvestimentoRequest;
import dev.Pedro.controle_gastos.api.dto.InvestimentoResponse;
import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import dev.Pedro.controle_gastos.enums.PeriodicidadeTaxa;
import dev.Pedro.controle_gastos.enums.TipoInvestimento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient

 class InvestimentoCrud {
    @Autowired
    private RestTestClient restTestClient;

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

        restTestClient
                .post()
                .uri("/scontg/investimentos")
                .body(request)
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

        restTestClient
                .post()
                .uri("/scontg/investimentos")
                .body(request)
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

        InvestimentoResponse criado = restTestClient
                .post()
                .uri("/scontg/investimentos")
                .body(request)
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

        restTestClient
                .put()
                .uri("/scontg/investimentos/" + criado.id())
                .body(requestAtualizado)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.descricao").isEqualTo(requestAtualizado.descricao())
                .jsonPath("$.categoria").isEqualTo(requestAtualizado.categoria().toString())
                .jsonPath("$.isentoIR").isEqualTo(true);
    }


}

