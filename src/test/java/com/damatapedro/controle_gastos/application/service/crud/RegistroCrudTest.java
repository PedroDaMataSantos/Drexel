package com.damatapedro.controle_gastos.application.service.crud;

import com.damatapedro.controle_gastos.application.dto.RegistroRequest;
import com.damatapedro.controle_gastos.application.exception.TipoRegistroInvalidoException;
import com.damatapedro.controle_gastos.application.service.RegistroService;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaRegistro;
import com.damatapedro.controle_gastos.domain.enumeration.TipoRegistro;
import com.damatapedro.controle_gastos.infrastructure.repository.RegistroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RegistroCrudTest {

    @Autowired
    private RegistroService registroService;

    @Autowired
    private RegistroRepository registroRepository;

    @BeforeEach
    void limparRegistros() {
        registroRepository.deleteAll();
    }

    @Test
    void deveCriarEConsultarRegistro() {
        var request = entrada("Salário", new BigDecimal("3000.00"), null);

        var criado = registroService.create(request);
        var encontrado = registroService.findById(criado.id());

        assertNotNull(criado.id());
        assertEquals(LocalDate.now(), criado.data());
        assertEquals(criado, encontrado);
        assertTrue(registroRepository.existsById(criado.id()));
    }

    @Test
    void deveAtualizarRegistro() {
        var criado = registroService.create(
                entrada("Salário", new BigDecimal("3000.00"), LocalDate.now().minusDays(1))
        );
        var atualizadoRequest = new RegistroRequest(
                TipoRegistro.SAIDA,
                CategoriaRegistro.ALIMENTACAO,
                "Mercado",
                new BigDecimal("250.00"),
                LocalDate.now()
        );

        var atualizado = registroService.update(criado.id(), atualizadoRequest);

        assertEquals(TipoRegistro.SAIDA, atualizado.tipoRegistro());
        assertEquals(CategoriaRegistro.ALIMENTACAO, atualizado.categoria());
        assertEquals("Mercado", atualizado.descricao());
        assertEquals(0, new BigDecimal("250.00").compareTo(atualizado.valor()));
        assertEquals(LocalDate.now(), atualizado.data());
    }

    @Test
    void deveExcluirRegistro() {
        var criado = registroService.create(
                entrada("Salário", new BigDecimal("3000.00"), LocalDate.now())
        );

        registroService.delete(criado.id());

        assertFalse(registroRepository.existsById(criado.id()));
    }

    @Test
    void deveFiltrarRegistros() {
        LocalDate hoje = LocalDate.now();
        registroService.create(entrada("Salário mensal", new BigDecimal("3000.00"), hoje));
        registroService.create(new RegistroRequest(
                TipoRegistro.SAIDA,
                CategoriaRegistro.ALIMENTACAO,
                "Mercado mensal",
                new BigDecimal("250.00"),
                hoje.minusDays(1)
        ));

        assertEquals(2, registroService.findAll().size());
        assertEquals(1, registroService.findByTipo(TipoRegistro.ENTRADA).size());
        assertEquals(1, registroService.findByCategoria(CategoriaRegistro.ALIMENTACAO).size());
        assertEquals(2, registroService.findByDescricao("mensal").size());
        assertEquals(2, registroService.findByPeriodo(hoje.minusDays(1), hoje).size());
    }

    @Test
    void deveRejeitarCategoriaIncompativelComTipo() {
        var request = new RegistroRequest(
                TipoRegistro.ENTRADA,
                CategoriaRegistro.ALIMENTACAO,
                "Categoria incompatível",
                new BigDecimal("100.00"),
                LocalDate.now()
        );

        var erro = assertThrows(TipoRegistroInvalidoException.class, () -> registroService.create(request));

        assertEquals("Essa categoria é incompatível com o tipo de registro selecionado", erro.getMessage());
        assertEquals(0, registroRepository.count());
    }

    private RegistroRequest entrada(String descricao, BigDecimal valor, LocalDate data) {
        return new RegistroRequest(
                TipoRegistro.ENTRADA,
                CategoriaRegistro.SALARIO,
                descricao,
                valor,
                data
        );
    }
}
