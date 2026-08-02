package dev.Pedro.controle_gastos.api.service.crud;

import dev.Pedro.controle_gastos.api.dto.InvestimentoRequest;
import dev.Pedro.controle_gastos.domain.entity.Investimento;
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

 class Crud {
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
                true,
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
                .jsonPath("$.isentoIR").isEqualTo(request.isentoIR())
                .jsonPath("$.taxaJuros").isEqualTo(request.taxaJuros())
                .jsonPath("$.periodicidadeTaxa").isEqualTo(request.periodicidadeTaxa().toString());

    }

    @Test
    void testCreateInvestimentoFailure() {
        var request = new InvestimentoRequest(
                "teste automatizado",
                null,
                null,
                null,
                null,
                true,
                null,
                null);

        restTestClient
                .post()
                .uri("/scontg/investimentos")
                .body(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}

