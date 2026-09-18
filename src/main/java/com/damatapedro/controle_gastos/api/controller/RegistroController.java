package com.damatapedro.controle_gastos.api.controller;


import com.damatapedro.controle_gastos.application.dto.InvestimentoRequest;
import com.damatapedro.controle_gastos.application.dto.InvestimentoResponse;
import com.damatapedro.controle_gastos.application.dto.RegistroRequest;
import com.damatapedro.controle_gastos.application.dto.RegistroResponse;
import com.damatapedro.controle_gastos.application.service.RegistroService;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaRegistro;
import com.damatapedro.controle_gastos.domain.enumeration.TipoRegistro;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/drexel/registros")

public class RegistroController {

    private final RegistroService service;

    public RegistroController(RegistroService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse create(@RequestBody RegistroRequest registroRequest) {

        return service.create(registroRequest);
    }

    @PostMapping("/aportar")
    @ResponseStatus(HttpStatus.CREATED)
    public InvestimentoResponse investir(@RequestBody InvestimentoRequest request) {
        return service.investir(request);
    }

    @PutMapping("/{id}")
    public RegistroResponse update(@PathVariable Long id,@RequestBody RegistroRequest req) {
        return service.update(id, req);

    }

    @GetMapping
    public List<RegistroResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public RegistroResponse getById(@PathVariable Long id) {
        return service.findById(id);

    }

    @GetMapping("/categoria/{categoria}")
    public List<RegistroResponse> getByCategoria(@PathVariable CategoriaRegistro categoria) {
        return service.findByCategoria(categoria);

    }

    @GetMapping("/tipo/{tipo}")
    public List<RegistroResponse> getByTipo(@PathVariable TipoRegistro tipo) {
        return service.findByTipo(tipo);

    }

    @GetMapping("/descricao/{descricao}")

    public List<RegistroResponse> getByDescricao(@PathVariable String descricao) {
        return service.findByDescricao(descricao);

    }



    @GetMapping("/periodo")
    public List<RegistroResponse> getByPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {

        return service.findByPeriodo(inicio, fim);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)

    public void delete(@PathVariable Long id) {
        service.delete(id);

    }


}
