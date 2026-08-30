package dev.Pedro.controle_gastos.api.service;


import dev.Pedro.controle_gastos.api.dto.InvestimentoResponse;
import dev.Pedro.controle_gastos.api.dto.RegistroRequest;
import dev.Pedro.controle_gastos.api.dto.RegistroResponse;
import dev.Pedro.controle_gastos.domain.entity.Investimento.Investimento;
import dev.Pedro.controle_gastos.domain.entity.Registro;
import dev.Pedro.controle_gastos.domain.repository.InvestimentoRepository;
import dev.Pedro.controle_gastos.domain.repository.RegistroRepository;
import dev.Pedro.controle_gastos.enums.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional

public class RegistroService {


    private final RegistroRepository repository;
    private final InvestimentoRepository investimentoRepository;
    private final DashboardService dashboardService;
    private final InvestimentoService investimentoService;

    public RegistroService(RegistroRepository repository, InvestimentoRepository investimentoRepository, DashboardService dashboardService, InvestimentoService investimentoService) {
        this.repository = repository;
        this.investimentoRepository = investimentoRepository;
        this.dashboardService = dashboardService;
        this.investimentoService = investimentoService;
    }

    //Create

    public RegistroResponse create(RegistroRequest registroRequest) {

        validarCategoria_Tipo(registroRequest);


        Registro registro = toEntity(registroRequest);

        return toResponse(repository.save(registro));
    }

    public RegistroResponse update(Long id, RegistroRequest registroRequest) {


        validarCategoria_Tipo(registroRequest);

        //Valida se o registro existe e retorna o erro
        Registro registroExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado"));

        //Update
        registroExistente.setTipoRegistro(registroRequest.tipoRegistro());

        registroExistente.setCategoria(registroRequest.categoria());

        registroExistente.setDescricao(registroRequest.descricao());

        registroExistente.setValor(registroRequest.valor());

        //Impede que a data seja apagada e passada uma data null
        if (registroRequest.data() != null) {
            registroExistente.setData(registroRequest.data());
        }

        //Salva as novas entradas
        return toResponse(repository.save(registroExistente));

    }

    public void delete(Long id) {

        //Verifica se existe
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro não encontrada. id=" + id);
        }
        repository.deleteById(id);
    }


    //Readers
    public RegistroResponse findById(Long id) {

        Registro registro = repository.findById(id).
                orElseThrow(() -> new RuntimeException("Registro não encontrado"));

        return toResponse(registro);

    }

    public List<RegistroResponse> findByCategoria(CategoriaRegistro categoria) {

        return listResponse(repository.findByCategoria(categoria));

    }

    public List<RegistroResponse> findByPeriodo(LocalDate inicio, LocalDate fim) {

        return listResponse(repository.findByDataBetween(inicio, fim));

    }

    public List<RegistroResponse> findByTipo(TipoRegistro tipo) {

        return listResponse(repository.findByTipoRegistro(tipo));
    }


    public List<RegistroResponse> findAll() {

        return listResponse(repository.findAll());

    }


    public  List<RegistroResponse> findByDescricao(String descricao){

        return listResponse(repository.findByDescricaoContainingIgnoreCase(descricao));
    }



    public InvestimentoResponse investir(BigDecimal valorAplicado, CategoriaInvestimento categoria, String descricao,
                                         boolean isentoIR, BigDecimal taxaJuros, PeriodicidadeTaxa periodicidadeTaxa) {

        if (valorAplicado.compareTo(BigDecimal.ZERO) <= 0 || valorAplicado.compareTo(dashboardService.saldoTotal()) > 0) {
            throw new RuntimeException("O valor deve ser maior que zero.");
        }


        if (categoria == CategoriaInvestimento.OUTROS) {
            taxaJuros = BigDecimal.ZERO;
            periodicidadeTaxa = null;
            isentoIR = true;   // OUTROS não rende, então não há IR mesmo
        }


        if (categoria == CategoriaInvestimento.LCI
                || categoria == CategoriaInvestimento.LCA
                || categoria == CategoriaInvestimento.POUPANCA) {
            isentoIR = true;
        }

        Investimento investimento = new Investimento(
                descricao,
                valorAplicado,
                LocalDate.now(),
                categoria,
                TipoInvestimento.APORTE,
                isentoIR,
                taxaJuros,
                periodicidadeTaxa);



        return investimentoService.toResponse(investimentoRepository.save(investimento));
    }




    //Valida se a Categoria de registro é compatível ao Tipo

    public void validarCategoria_Tipo(RegistroRequest registroRequest) {

        //Verifica se o tipo pré definido na Categoria do enum bate com o tipo de registro escolhido

        if (!registroRequest.categoria().getTipo().equals(registroRequest.tipoRegistro())) {
            throw new RuntimeException("Essa categoria é incompatível com o tipo de registro selecionado");
        }
    }


    //Transforma a entrada (Request) em entity para a operação no DB
    private Registro toEntity(RegistroRequest registroRequest) {

        LocalDate data;

        if (registroRequest.data() == null) {
            data = LocalDate.now();
        } else {
            data = registroRequest.data();
        }

        return new Registro(
                registroRequest.tipoRegistro(),
                registroRequest.categoria(),
                registroRequest.descricao(),
                registroRequest.valor(),
                data
        );
    }
    //Package - private
    //Transforma a saída em Response para não devolver na forma de entity
     RegistroResponse toResponse(Registro registro) {

        return new RegistroResponse(
                registro.getId(),
                registro.getTipoRegistro(),
                registro.getCategoria(),
                registro.getDescricao(),
                registro.getValor(),
                registro.getData()
        );

    }

    private List<RegistroResponse> listResponse(List<Registro> registros) {

        List<RegistroResponse> responses = new ArrayList<>();

        for (Registro registro : registros) {

            responses.add(toResponse(registro));
        }
        return responses;
    }


}
