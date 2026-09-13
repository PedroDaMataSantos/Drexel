package dev.Pedro.controle_gastos.api.service;

import dev.Pedro.controle_gastos.api.dto.InvestimentoResponse;
import dev.Pedro.controle_gastos.api.dto.mapper.InvestimentoMapper;
import dev.Pedro.controle_gastos.domain.entity.Investimento;
import dev.Pedro.controle_gastos.domain.repository.InvestimentoRepository;
import dev.Pedro.controle_gastos.enums.*;
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

    public InvestimentoService(InvestimentoRepository repository, InvestimentoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }




    public void delete(Long id) {

        if (!repository.existsById(id)) {

            throw new RuntimeException("Investimento não encontrada. id=" + id);
        }


        repository.deleteById(id);

    }

    public InvestimentoResponse findById(Long id) {

        Investimento investimento = repository.findById(id).
                orElseThrow(() -> new RuntimeException("Investimento não encontrado"));


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




