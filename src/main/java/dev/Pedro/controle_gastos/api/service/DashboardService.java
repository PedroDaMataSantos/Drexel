package dev.Pedro.controle_gastos.api.service;

import dev.Pedro.controle_gastos.api.dto.DashboardResponse;
import dev.Pedro.controle_gastos.domain.entity.Registro;
import dev.Pedro.controle_gastos.domain.repository.InvestimentoRepository;
import dev.Pedro.controle_gastos.domain.repository.RegistroRepository;
import dev.Pedro.controle_gastos.enums.CategoriaRegistro;
import dev.Pedro.controle_gastos.enums.TipoRegistro;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static dev.Pedro.controle_gastos.api.service.calculo.RendimentoCalculator.calcularRendimento;


@Service
@Transactional

public class DashboardService {

    private final RegistroRepository registroRepository;
    private final InvestimentoRepository investimentoRepository;

    public DashboardService(RegistroRepository registroRepository, InvestimentoRepository investimentoRepository) {
        this.registroRepository = registroRepository;
        this.investimentoRepository = investimentoRepository;

    }

    public DashboardResponse dashboard() {

        return new DashboardResponse(
                saldoTotal(),
                saldoMensal(),
                totalSaida(),
                saidaMensal(),
                totalEntrada(),
                entradaMensal(),
                totalInvestido(),
                investimentoMensal(),
                patrimonio(),
                rendimentoTotal(),
                rendimentoMensal(),
                gastoCategoriaTotal(),
                gastoCategoriaMensal()

        );
    }

    public BigDecimal totalInvestido() {

        return somaInvestimentos(investimentoRepository.findAll());

    }

    public BigDecimal totalEntrada() {

        return somaRegistros(registroRepository.findByTipoRegistro(TipoRegistro.ENTRADA));

    }

    public BigDecimal totalSaida() {

        return somaRegistros(registroRepository.findByTipoRegistro(TipoRegistro.SAIDA));
    }

    public BigDecimal saldoTotal() {

        return totalEntrada().subtract(totalSaida()).subtract(aporte());
    }

    //Map usa chave ou invés de indices para encontrar valor , no caso minha Chave é a CATEGORIA
    public Map<CategoriaRegistro, BigDecimal> gastoCategoriaTotal() {

        //Map <Chave,Valor>                        novo Tipo de Map que so aceita ENUM como chave (Informa qual enum será usado como chave).
        Map<CategoriaRegistro, BigDecimal> gastos = new EnumMap<>(CategoriaRegistro.class);

        for (CategoriaRegistro categoria : CategoriaRegistro.values()) {

            if (categoria.getTipo() != TipoRegistro.SAIDA) {
                continue;
            }
                //COLOQUE (CHAVE , VALOR)
                gastos.put(categoria, somaGastosPorCategoria(categoria));

        }

        return gastos;
    }

    public BigDecimal aporte(){

        return somaAporte();
    }

    public BigDecimal patrimonio() {

        return saldoTotal().add(totalInvestido());
    }

    public BigDecimal rendimentoTotal() {
        return somaRendimentos(investimentoRepository.findAll());
    }

    public BigDecimal entradaMensal() {

        return somaRegistroMensal(TipoRegistro.ENTRADA);
    }

    public BigDecimal saidaMensal() {

        return somaRegistroMensal(TipoRegistro.SAIDA);
    }

    public BigDecimal investimentoMensal(){

        return somaInvestimentoMensal();
    }


    public BigDecimal saldoMensal() {

        return entradaMensal().subtract(saidaMensal()).subtract(aporteMensal());

    }

    public BigDecimal aporteMensal() {

        return somaAporteMensal();
    }

  public BigDecimal rendimentoMensal() {

        return somaRendimentoMensal();
  }



    public Map<CategoriaRegistro, BigDecimal> gastoCategoriaMensal() {

        Map<CategoriaRegistro, BigDecimal> gastos = new EnumMap<>(CategoriaRegistro.class);

        for (CategoriaRegistro categoria : CategoriaRegistro.values()) {

            if (categoria.getTipo() != TipoRegistro.SAIDA) {
                continue;
            }
                gastos.put(categoria, somaGastosCategoriaMensal(categoria)
                );
            }


        return gastos;
    }

    //CALCULOS


    private BigDecimal somaRegistros(List<Registro> registros) {

        BigDecimal total = BigDecimal.ZERO;

        for (Registro registro : registros) {

            total = total.add(registro.getValor());
        }

        return total;

    }

    private BigDecimal somaInvestimentos(List<Investimento> investimentos) {

        BigDecimal total = BigDecimal.ZERO;

        for (Investimento investimento : investimentos) {

            total = total.add(investimento.getValorAplicado());

        }

        return total;
    }
    private BigDecimal somaRendimentos(List<Investimento> investimentos) {

        BigDecimal total = BigDecimal.ZERO;

        for (Investimento investimento : investimentos) {
            total = total.add(calcularRendimento(investimento));
        }
        return total;
    }


    private BigDecimal somaRegistroMensal(TipoRegistro tipo){

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());


        return somaRegistros(registroRepository.findByTipoRegistroAndDataBetween(
                tipo,
                inicio,
                fim
        ));

    }



    private BigDecimal somaGastosPorCategoria(CategoriaRegistro categoria){

        if (categoria.getTipo() != TipoRegistro.SAIDA) {
            throw new IllegalArgumentException(
                    "A categoria " + categoria + " não pertence ao tipo SAIDA.");
        }

        return somaRegistros(registroRepository.findByCategoria(categoria));


    }

    private BigDecimal somaInvestimentoMensal(){

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());

        return somaInvestimentos(investimentoRepository.findByDataBetween(inicio,fim));
    }


    private BigDecimal somaAporte(){

        return somaInvestimentos(investimentoRepository.findByTipo(TipoInvestimento.APORTE));

    }

    private BigDecimal somaAporteMensal(){

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());

        return somaInvestimentos(investimentoRepository.findByTipoAndDataBetween(
                TipoInvestimento.APORTE,
                inicio,
                fim
        ));

    }



    private BigDecimal somaGastosCategoriaMensal(CategoriaRegistro categoria){

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());

         return somaRegistros(registroRepository.findByCategoriaAndDataBetween(categoria,inicio,fim));



    }

    private BigDecimal somaRendimentoMensal(){

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());

        return somaRendimentos(investimentoRepository.findByDataBetween(inicio,fim));

    }

}



