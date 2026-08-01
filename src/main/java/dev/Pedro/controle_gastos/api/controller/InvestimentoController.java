package dev.Pedro.controle_gastos.api.controller;

import dev.Pedro.controle_gastos.api.dto.InvestimentoRequest;
import dev.Pedro.controle_gastos.api.dto.InvestimentoResponse;
import dev.Pedro.controle_gastos.api.dto.PrevisaoSaqueResponse;
import dev.Pedro.controle_gastos.api.dto.RegistroResponse;
import dev.Pedro.controle_gastos.api.service.InvestimentoService;
import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import dev.Pedro.controle_gastos.enums.TipoInvestimento;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/scontg/investimentos")

public class InvestimentoController {

    private final InvestimentoService service;

    public InvestimentoController(InvestimentoService service) {
        this.service = service;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvestimentoResponse create(@RequestBody InvestimentoRequest investimentoRequest) {

        return service.create(investimentoRequest);
    }

    @PostMapping("/{id}/sacar")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse sacar(@PathVariable Long id, @RequestParam BigDecimal valor) {

        return service.sacar(id, valor);
    }

    @PutMapping("/{id}")
    public InvestimentoResponse update(@PathVariable Long id,@RequestBody InvestimentoRequest investimentoRequest) {

        return service.update(id, investimentoRequest);
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

    @GetMapping("/tipo/{tipo}")
    public List<InvestimentoResponse> buscarPorTipo(
            @PathVariable TipoInvestimento tipo
    ) {

        return service.findByTipo(tipo);

    }

    @GetMapping
    public List<InvestimentoResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}/previsao-saque")
    public PrevisaoSaqueResponse previsaoSaque(@PathVariable Long id) {
        return service.previsaoSaque(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)


    public void delete(@PathVariable Long id) {

        service.delete(id);
    }


}
