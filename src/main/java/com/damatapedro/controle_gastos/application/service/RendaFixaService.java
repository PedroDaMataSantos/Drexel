package com.damatapedro.controle_gastos.application.service;

import com.damatapedro.controle_gastos.application.dto.*;
import com.damatapedro.controle_gastos.application.mapper.InvestimentoMapper;
import com.damatapedro.controle_gastos.domain.entity.Registro;
import com.damatapedro.controle_gastos.domain.entity.RendaFixa;
import com.damatapedro.controle_gastos.infrastructure.repository.RegistroRepository;
import com.damatapedro.controle_gastos.infrastructure.repository.RendaFixaRepository;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaRegistro;
import com.damatapedro.controle_gastos.domain.enumeration.TipoRegistro;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.damatapedro.controle_gastos.application.calculation.PrevisaoSaqueCalculator.calcularInformacoesSaque;
import static com.damatapedro.controle_gastos.application.calculation.PrevisaoSaqueCalculator.valorDisponivelSaque;


@Service
@Transactional

public class RendaFixaService {


    private final RendaFixaRepository repository;
    private final RegistroRepository registroRepository;
    private final InvestimentoMapper mapper;

    public RendaFixaService(RendaFixaRepository repository, RegistroRepository registroRepository, InvestimentoMapper mapper) {
        this.repository = repository;
        this.registroRepository = registroRepository;
        this.mapper = mapper;
    }


    public InvestimentoResponse create(RendaFixaRequest rendaFixaRequest, boolean isAporte) {
        RendaFixa rendaFixa = toEntity(rendaFixaRequest, isAporte);

        return mapper.toResponse(repository.save(rendaFixa));
    }


    public InvestimentoResponse update(Long id, RendaFixaRequest rendaFixaRequest) {

        RendaFixa rendaFixaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investimento não encontrado"));

        rendaFixaExistente.setDescricao(rendaFixaRequest.descricao());

        if (rendaFixaRequest.data() != null) {

            rendaFixaExistente.setData(rendaFixaRequest.data());
        }

        rendaFixaExistente.setCategoria(rendaFixaRequest.categoria());


        rendaFixaExistente.setIsentoIR(rendaFixaRequest.categoria().isIsento());

        return mapper.toResponse(repository.save(rendaFixaExistente));
    }


    // Cria o response diretamente para evitar dependência circular com RegistroService.
    public RegistroResponse sacar(Long id, BigDecimal valor) {

        RendaFixa rendaFixaExistente = repository.findById(id).
                orElseThrow(() -> new RuntimeException("Investimento não encontrado"));

        BigDecimal disponivel = valorDisponivelSaque(rendaFixaExistente);

        if (disponivel.compareTo(valor) < 0 || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        rendaFixaExistente.setSaldoAtual(disponivel.subtract(valor));   // reusa
        rendaFixaExistente.setUltimoSaque(LocalDate.now());

        repository.save(rendaFixaExistente);

        Registro registro = new Registro(
                TipoRegistro.ENTRADA,
                CategoriaRegistro.INVESTIMENTO,
                rendaFixaExistente.getDescricao(),
                valor,
                LocalDate.now()
        );

        Registro registroSalvo = registroRepository.save(registro);

        return new RegistroResponse(
                registroSalvo.getId(),
                registroSalvo.getTipoRegistro(),
                registroSalvo.getCategoria(),
                registroSalvo.getDescricao(),
                registroSalvo.getValor(),
                registroSalvo.getData()
        );
    }


    public PrevisaoSaqueResponse previsaoSaque(Long id) {

        RendaFixa rendaFixa = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investimento não encontrado"));

        return calcularInformacoesSaque(rendaFixa);
    }

    private RendaFixa toEntity(RendaFixaRequest rendaFixaRequest, boolean isAporte) {

        LocalDate data = rendaFixaRequest.data()==null
                ? LocalDate.now()
                :rendaFixaRequest.data();



        RendaFixa rendafixa = new RendaFixa(
                rendaFixaRequest.descricao(),
                rendaFixaRequest.valorAplicado(),
                data,
                rendaFixaRequest.categoria(),
                isAporte,
                rendaFixaRequest.categoria().isIsento(),
                rendaFixaRequest.taxaJuros(),
                rendaFixaRequest.periodicidadeTaxa());

        return rendafixa;
    }
}
