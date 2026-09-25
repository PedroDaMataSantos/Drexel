package com.damatapedro.controle_gastos.application.calculation;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.damatapedro.controle_gastos.domain.entity.RendaFixa;
import com.damatapedro.controle_gastos.domain.enumeration.PeriodicidadeTaxa;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class RendimentoCalculator {

    public static BigDecimal calcularTaxaDiaria(BigDecimal taxa, PeriodicidadeTaxa periodicidadeTaxa) {

        //Formula do jurosComposto JC = (1+i) ^ (1/n) - 1

        //Define em qual "casa" para
        MathContext mc = MathContext.DECIMAL64;

        BigDecimal taxaDecimal = taxa.divide(BigDecimal.valueOf(100), mc); //Converte a taxa em decimal

        BigDecimal base = BigDecimal.ONE.add(taxaDecimal); // (1 + i)

        BigDecimal diasPeriodo = (periodicidadeTaxa == PeriodicidadeTaxa.ANUAL) // Define se a n é 365 ou 30
                ? BigDecimal.valueOf(365)
                : BigDecimal.valueOf(30);
        BigDecimal expoente = BigDecimal.ONE.divide(diasPeriodo, mc); //(1/n)


        return BigDecimalMath.pow(base, expoente, mc).subtract(BigDecimal.ONE);//(1+i) ^ (1/n) - 1

    }

    public static BigDecimal  valorBrutoFinal(RendaFixa rendaFixa) {


        MathContext mc = MathContext.DECIMAL64;

        BigDecimal taxaDiaria = calcularTaxaDiaria(rendaFixa.getTaxaJuros(),rendaFixa.getPeriodicidadeTaxa());
        Long diasCorridos = calcularDiasCorridos(dataReferencia(rendaFixa.getData(),rendaFixa.getUltimoSaque()), LocalDate.now());


        //Formula juros compostos = valor × (1 + taxaDiária) ^ diasCorridos

        BigDecimal base = BigDecimal.valueOf(1.0).add(taxaDiaria); //(1 + taxaDiaria)
        BigDecimal fatorPotencia = base.pow(diasCorridos.intValue(),mc);//(1+taxaDiaria)^diasCorridos




        return rendaFixa.getSaldoAtual().multiply(fatorPotencia);//valorAplicado × (1 + taxaDiária) ^ diasCorridos;


    }

    public static BigDecimal calcularRendimento(RendaFixa rendaFixa) {

        BigDecimal valorBruto = valorBrutoFinal(rendaFixa);

        return valorBruto.subtract(
                rendaFixa.getPrincipalRemanescente()
        );
    }



    public static LocalDate dataReferencia(LocalDate dataAplicacao, LocalDate ultimoSaque) {
        return ultimoSaque == null
                ? dataAplicacao
                : ultimoSaque;
    }


    public static Long calcularDiasCorridos(LocalDate aplicacao, LocalDate saque) {

        return DAYS.between(aplicacao, saque);
    }





}
