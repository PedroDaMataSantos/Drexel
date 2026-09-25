package com.damatapedro.controle_gastos.api.controller;

import com.damatapedro.controle_gastos.application.dto.InvestimentoResponse;
import com.damatapedro.controle_gastos.application.dto.PrevisaoResgateParcialResponse;
import com.damatapedro.controle_gastos.application.dto.RegistroResponse;
import com.damatapedro.controle_gastos.application.dto.RendaFixaRequest;
import com.damatapedro.controle_gastos.application.service.RendaFixaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/drexel/investimento/renda-fixa")
public class RendaFixaController {

    private final RendaFixaService service;

    public RendaFixaController(RendaFixaService service) {
        this.service = service;
    }

    @PostMapping("/{id}/sacar")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse sacar(
            @PathVariable Long id,
            @RequestParam BigDecimal valor
    ) {
        return service.sacar(id, valor);
    }

    @PutMapping("/{id}")
    public InvestimentoResponse update(@PathVariable Long id, @RequestBody RendaFixaRequest request){

        return service.update(id, request);
    }

    @GetMapping("/{id}/previsao-saque")
    public PrevisaoResgateParcialResponse previsaoSaque(@PathVariable Long id) {
        return service.previsaoSaque(id);
    }
}
