package com.damatapedro.controle_gastos.application.service;

import com.damatapedro.controle_gastos.application.dto.InvestimentoRequest;
import com.damatapedro.controle_gastos.application.dto.InvestimentoResponse;
import com.damatapedro.controle_gastos.application.dto.RendaFixaRequest;
import com.damatapedro.controle_gastos.application.exception.InvestimentoNotFoundException;
import com.damatapedro.controle_gastos.application.exception.TipoInvestimentoInvalidoException;
import com.damatapedro.controle_gastos.application.mapper.InvestimentoMapper;
import com.damatapedro.controle_gastos.domain.entity.Investimento;
import com.damatapedro.controle_gastos.infrastructure.repository.InvestimentoRepository;
import com.damatapedro.controle_gastos.domain.enumeration.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional

public class InvestimentoService {


    private final InvestimentoRepository repository;
    private final InvestimentoMapper mapper;
    private final RendaFixaService rendaFixaService;

    public InvestimentoService(InvestimentoRepository repository, InvestimentoMapper mapper, RendaFixaService rendaFixaService) {
        this.repository = repository;
        this.mapper = mapper;
        this.rendaFixaService = rendaFixaService;
    }

    public InvestimentoResponse create(InvestimentoRequest request, boolean isAporte) {
        return switch (request) {
            case RendaFixaRequest rendaFixa ->
                    rendaFixaService.create(rendaFixa, isAporte);

//            case RendaVariavelRequest rendaVariavel ->
//                    rendaVariavelService.create(rendaVariavel, isAporte);

            default -> throw new TipoInvestimentoInvalidoException(
                    "Tipo de investimento não suportado."
            );
        };
    }

    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new InvestimentoNotFoundException("Investimento não encontrada. id=" + id);
        }

        repository.deleteById(id);

    }

    public InvestimentoResponse findById(Long id) {

        Investimento investimento = repository.findById(id).
                orElseThrow(() -> new InvestimentoNotFoundException("Investimento não encontrado"));


        return mapper.toResponse(investimento);
    }

    public List<InvestimentoResponse> findByCategoria(CategoriaInvestimento categoria) {

        return listResponse(repository.findByCategoria(categoria));

    }

    public List<InvestimentoResponse> findByPeriodo(LocalDate inicio, LocalDate fim) {

        return listResponse(repository.findByDataBetween(inicio, fim));
    }

    public List<InvestimentoResponse> findAll() {

        return listResponse(repository.findAll());
    }

    public List<InvestimentoResponse> findByIsAporte(boolean isAporte) {

        return listResponse(repository.findByisAporte(isAporte));

    }

    private List<InvestimentoResponse> listResponse(List<Investimento> investimentos) {

        List<InvestimentoResponse> responses = new ArrayList<>();

        for (Investimento investimento : investimentos) {

            responses.add(mapper.toResponse(investimento));
        }

        return responses;
    }
}




