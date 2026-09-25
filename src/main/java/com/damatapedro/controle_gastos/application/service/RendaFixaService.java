package com.damatapedro.controle_gastos.application.service;

import com.damatapedro.controle_gastos.application.dto.*;
import com.damatapedro.controle_gastos.application.exception.*;
import com.damatapedro.controle_gastos.application.mapper.InvestimentoMapper;
import com.damatapedro.controle_gastos.domain.entity.Registro;
import com.damatapedro.controle_gastos.domain.entity.RendaFixa;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaInvestimento;
import com.damatapedro.controle_gastos.infrastructure.repository.RegistroRepository;
import com.damatapedro.controle_gastos.infrastructure.repository.RendaFixaRepository;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaRegistro;
import com.damatapedro.controle_gastos.domain.enumeration.TipoRegistro;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

import static com.damatapedro.controle_gastos.application.calculation.RendaFixaCalculator.calcularInformacoesSaque;
import static com.damatapedro.controle_gastos.application.calculation.RendaFixaCalculator.valorDisponivelSaque;
import static com.damatapedro.controle_gastos.application.calculation.RendimentoCalculator.valorBrutoFinal;


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
                .orElseThrow(() -> new InvestimentoNotFoundException());

        rendaFixaExistente.setDescricao(rendaFixaRequest.descricao());

        if (rendaFixaRequest.data() != null) {
            rendaFixaExistente.setData(rendaFixaRequest.data());
        }

        validarCategoria(rendaFixaRequest.categoria());

        rendaFixaExistente.setCategoria(rendaFixaRequest.categoria());
        rendaFixaExistente .setPeriodicidadeTaxa(rendaFixaRequest.periodicidadeTaxa());
        rendaFixaExistente.setTaxaJuros(rendaFixaRequest.taxaJuros());
        rendaFixaExistente.setIsentoIR(rendaFixaRequest.categoria().isIsento());

        return mapper.toResponse(repository.save(rendaFixaExistente));
    }

    // Cria o response diretamente para evitar dependência circular com RegistroService.
    public RegistroResponse sacar(Long id, BigDecimal valor) {

        RendaFixa rendaFixaExistente = repository.findById(id).
                orElseThrow(() -> new InvestimentoNotFoundException());

        PrevisaoResgateParcialResponse informacoes =
                calcularInformacoesSaque(rendaFixaExistente);

        BigDecimal valorBruto = informacoes.valorBruto();
        BigDecimal disponivel = informacoes.valorDisponivel();

        if ( valor.compareTo(BigDecimal.ZERO) <= 0) {
        throw new ValorInvalidoException();
    }

    if (disponivel.compareTo(valor) < 0 ) {
        throw new ValorResgateInsuficienteException("O valor disponivel para saque é menor que o valor digitado. Disponivel:"
                + disponivel);
    }

        BigDecimal proporcaoResgatada = valor.divide(disponivel, MathContext.DECIMAL64);
        BigDecimal principalResgatada = rendaFixaExistente.getPrincipalRemanescente().multiply(proporcaoResgatada);
        BigDecimal novoPrincipal = rendaFixaExistente.getPrincipalRemanescente().subtract(principalResgatada).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal valorBrutoResgatado = valorBruto.multiply(proporcaoResgatada);
        BigDecimal novoSaldo =valorBruto.subtract(valorBrutoResgatado).setScale(2, RoundingMode.HALF_EVEN);


        rendaFixaExistente.setPrincipalRemanescente(novoPrincipal);
        rendaFixaExistente.setSaldoAtual(novoSaldo);
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

    public PrevisaoResgateParcialResponse previsaoSaque(Long id) {

        RendaFixa rendaFixa = repository.findById(id)
                .orElseThrow(() -> new InvestimentoNotFoundException());

        return calcularInformacoesSaque(rendaFixa);
    }

    private void validarCategoria(CategoriaInvestimento categoria) {

        if (categoria == null) {
            throw new CategoriaInvestimentoInvalidoException(
                    "A categoria é obrigatória."
            );
        }

        if (!categoria.getTipoInvestimento().equals(RendaFixa.class)) {
            throw new CategoriaInvestimentoInvalidoException(
                    "A categoria " + categoria
                            + " não pertence à renda fixa."
            );
        }
    }
    private RendaFixa toEntity(RendaFixaRequest rendaFixaRequest, boolean isAporte) {

        validarCategoria(rendaFixaRequest.categoria());

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
