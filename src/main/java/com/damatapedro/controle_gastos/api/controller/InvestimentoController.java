package com.damatapedro.controle_gastos.api.controller;

import com.damatapedro.controle_gastos.application.dto.*;
import com.damatapedro.controle_gastos.application.service.InvestimentoService;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/scontg/investimentos")

public class InvestimentoController {

    private final InvestimentoService service;

    public InvestimentoController(InvestimentoService service) {

        this.service = service;

    }

    @GetMapping("/{id}")
    public InvestimentoResponse getId(@PathVariable Long id) {

        return service.findById(id);


    }

    @GetMapping("/categoria/{categoria}")
    public List<InvestimentoResponse> getByCategoria(@PathVariable CategoriaInvestimento categoria) {

        return service.findByCategoria(categoria);

    }

    @GetMapping("/periodo")
    public List<InvestimentoResponse> getByPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim
    ) {

        return service.findByPeriodo(inicio, fim);
    }

    @GetMapping("/tipo/{isAporte}")
    public List<InvestimentoResponse> buscarPorisAporte(
            @PathVariable boolean isAporte
    ) {

        return service.findByIsAporte(isAporte);

    }

    @GetMapping
    public List<InvestimentoResponse> findAll() {

        return service.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)

    public void delete(@PathVariable Long id) {

        service.delete(id);
    }


}
