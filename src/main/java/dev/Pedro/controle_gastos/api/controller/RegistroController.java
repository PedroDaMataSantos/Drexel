package dev.Pedro.controle_gastos.api.controller;


import dev.Pedro.controle_gastos.api.dto.InvestimentoResponse;
import dev.Pedro.controle_gastos.api.dto.RegistroRequest;
import dev.Pedro.controle_gastos.api.dto.RegistroResponse;
import dev.Pedro.controle_gastos.api.service.RegistroService;
import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import dev.Pedro.controle_gastos.enums.CategoriaRegistro;
import dev.Pedro.controle_gastos.enums.PeriodicidadeTaxa;
import dev.Pedro.controle_gastos.enums.TipoRegistro;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/scontg/registros")

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
    public InvestimentoResponse investir(@RequestParam BigDecimal valor,
                                         @RequestParam CategoriaInvestimento categoria,
                                         @RequestParam String descricao,
                                         @RequestParam BigDecimal taxaJuros,
                                         @RequestParam PeriodicidadeTaxa periodicidadeTaxa) {

        return service.investir(valor, categoria, descricao, taxaJuros, periodicidadeTaxa);
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
