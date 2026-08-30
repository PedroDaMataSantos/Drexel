package dev.Pedro.controle_gastos.api.service;

import dev.Pedro.controle_gastos.api.dto.InvestimentoRequest;
import dev.Pedro.controle_gastos.api.dto.InvestimentoResponse;
import dev.Pedro.controle_gastos.api.dto.PrevisaoSaqueResponse;
import dev.Pedro.controle_gastos.api.dto.RegistroResponse;
import dev.Pedro.controle_gastos.domain.entity.Investimento.Investimento;
import dev.Pedro.controle_gastos.domain.entity.Registro;
import dev.Pedro.controle_gastos.domain.repository.InvestimentoRepository;
import dev.Pedro.controle_gastos.domain.repository.RegistroRepository;
import dev.Pedro.controle_gastos.enums.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static dev.Pedro.controle_gastos.api.service.calculo.PrevisaoSaqueCalculator.*;
import static dev.Pedro.controle_gastos.api.service.calculo.RendimentoCalculator.calcularRendimento;


@Service
@Transactional

public class InvestimentoService {


    private final InvestimentoRepository repository;
    private final RegistroRepository registroRepository;

    public InvestimentoService(InvestimentoRepository repository, RegistroRepository registroRepository) {
        this.repository = repository;
        this.registroRepository = registroRepository;
    }

    public InvestimentoResponse create(InvestimentoRequest investimentoRequest) {


        Investimento investimento = toEntity(investimentoRequest);

        return toResponse(repository.save(investimento));

    }

    public InvestimentoResponse update(Long id, InvestimentoRequest investimentoRequest) {



        Investimento investimentoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investimento não encontrado"));


        investimentoExistente.setDescricao(investimentoRequest.descricao());

        if (investimentoRequest.data() != null) {

            investimentoExistente.setData(investimentoRequest.data());
        }



        investimentoExistente.setCategoria(investimentoRequest.categoria());

        if (investimentoRequest.categoria() != CategoriaInvestimento.OUTROS) {
            investimentoExistente.setTaxaJuros(investimentoRequest.taxaJuros());

            investimentoExistente.setPeriodicidadeTaxa(investimentoRequest.periodicidadeTaxa());

        }else{
            ajustarTaxaPeridiocidadeAutomaticamente(investimentoExistente,investimentoRequest.categoria());
        }

        investimentoExistente.setIsentoIR(calcularIsentoIR(investimentoRequest.categoria()));



        return toResponse(repository.save(investimentoExistente));
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


        return toResponse(investimento);
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

    public List<InvestimentoResponse> findByTipo(TipoInvestimento tipo) {

        return listResponse(repository.findByTipo(tipo));

    }

    //Ele tem que criar manualmente o Response porque se ele chamr o RegistroSercvice como o RegistroService chama essa classe , eles criam um loop e o spring trava
    public RegistroResponse sacar(Long id, BigDecimal valor) {

        Investimento investimentoExistente = repository.findById(id).
                orElseThrow(() -> new RuntimeException("Investimento não encontrado"));

        BigDecimal disponivel = valorDisponivelSaque(investimentoExistente);

        if (disponivel.compareTo(valor) < 0 || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor inválido");
        }

        investimentoExistente.setValorPosSaque(disponivel.subtract(valor));   // reusa
        investimentoExistente.setUltimoSaque(LocalDate.now());

        repository.save(investimentoExistente);

        Registro registro = new Registro(
                TipoRegistro.ENTRADA,
                CategoriaRegistro.INVESTIMENTO,
                investimentoExistente.getDescricao(),
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

        Investimento investimento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investimento não encontrado"));

        return calcularInformacoesSaque(investimento);
    }

    private boolean calcularIsentoIR(CategoriaInvestimento categoria) {
        return categoria == CategoriaInvestimento.LCI
                || categoria == CategoriaInvestimento.LCA
                || categoria == CategoriaInvestimento.POUPANCA
                || categoria == CategoriaInvestimento.OUTROS;
    }

    private void ajustarTaxaPeridiocidadeAutomaticamente(Investimento investimento, CategoriaInvestimento categoria) {
        if (categoria == CategoriaInvestimento.OUTROS) {
            investimento.setTaxaJuros(BigDecimal.ZERO);
            investimento.setPeriodicidadeTaxa(null);
        }
    }




    private Investimento toEntity(InvestimentoRequest investimentoRequest) {

        LocalDate data = investimentoRequest.data()==null
                ? LocalDate.now()
                :investimentoRequest.data();



        Investimento investimento = new Investimento(
                investimentoRequest.descricao(),
                investimentoRequest.valorAplicado(),
                data,
                investimentoRequest.categoria(),
                TipoInvestimento.INVESTIMENTO,
                calcularIsentoIR(investimentoRequest.categoria()),
                investimentoRequest.taxaJuros(),
                investimentoRequest.periodicidadeTaxa());

        ajustarTaxaPeridiocidadeAutomaticamente(investimento,investimento.getCategoria());

        return investimento;
    }


    //package-private
    InvestimentoResponse toResponse(Investimento investimento) {

        return new InvestimentoResponse(
                investimento.getId(),
                investimento.getDescricao(),
                investimento.getValorAplicado(),
                investimento.getValorPosSaque(),
                investimento.getData(),
                investimento.getCategoria(),
                investimento.getTipo(),
                investimento.isIsentoIR(),
                investimento.getTaxaJuros(),
                investimento.getPeriodicidadeTaxa(),
                investimento.getUltimoSaque(),
                calcularRendimento(investimento),
                calcularInformacoesSaque(investimento).valorBruto()
        );
    }

    private List<InvestimentoResponse> listResponse(List<Investimento> investimentos) {

        List<InvestimentoResponse> responses = new ArrayList<>();

        for (Investimento investimento : investimentos) {

            responses.add(toResponse(investimento));
        }

        return responses;
    }
}




