package dev.Pedro.controle_gastos.api.service;

import dev.Pedro.controle_gastos.api.dto.*;
import dev.Pedro.controle_gastos.api.dto.mapper.InvestimentoMapper;
import dev.Pedro.controle_gastos.domain.entity.Registro;
import dev.Pedro.controle_gastos.domain.entity.RendaFixa;
import dev.Pedro.controle_gastos.domain.repository.RegistroRepository;
import dev.Pedro.controle_gastos.domain.repository.RendaFixaRepository;
import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import dev.Pedro.controle_gastos.enums.CategoriaRegistro;
import dev.Pedro.controle_gastos.enums.TipoRegistro;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static dev.Pedro.controle_gastos.api.service.calculo.PrevisaoSaqueCalculator.calcularInformacoesSaque;
import static dev.Pedro.controle_gastos.api.service.calculo.PrevisaoSaqueCalculator.valorDisponivelSaque;


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


    public InvestimentoResponse create(RendaFixaRequest rendaFixaRequest) {

        RendaFixa rendaFixa = toEntity(rendaFixaRequest);

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

        if (rendaFixaRequest.categoria() != CategoriaInvestimento.OUTROS) {
            rendaFixaExistente.setTaxaJuros(rendaFixaRequest.taxaJuros());

            rendaFixaExistente.setPeriodicidadeTaxa(rendaFixaRequest.periodicidadeTaxa());

        }else{
            ajustarTaxaPeridiocidadeAutomaticamente(rendaFixaExistente,rendaFixaRequest.categoria());
        }

        rendaFixaExistente.setIsentoIR(rendaFixaRequest.categoria().isIsento());

        return mapper.toResponse(repository.save(rendaFixaExistente));
    }


    //Ele tem que criar manualmente o Response porque se ele chamr o RegistroSercvice como o RegistroService chama essa classe , eles criam um loop e o spring trava
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

    private void ajustarTaxaPeridiocidadeAutomaticamente(RendaFixa rendaFixa, CategoriaInvestimento categoria) {
        if (categoria == CategoriaInvestimento.OUTROS) {
            rendaFixa.setTaxaJuros(BigDecimal.ZERO);
            rendaFixa.setPeriodicidadeTaxa(null);
        }
    }




    private RendaFixa toEntity(RendaFixaRequest rendaFixaRequest) {

        LocalDate data = rendaFixaRequest.data()==null
                ? LocalDate.now()
                :rendaFixaRequest.data();



        RendaFixa rendafixa = new RendaFixa(
                rendaFixaRequest.descricao(),
                rendaFixaRequest.valorAplicado(),
                data,
                rendaFixaRequest.categoria(),
                rendaFixaRequest.isAporte(),
                rendaFixaRequest.categoria().isIsento(),
                rendaFixaRequest.taxaJuros(),
                rendaFixaRequest.periodicidadeTaxa());

        ajustarTaxaPeridiocidadeAutomaticamente(
                rendafixa,rendaFixaRequest.categoria());

        return rendafixa;
    }
}
