package dev.Pedro.controle_gastos.api.service.calculo;

import dev.Pedro.controle_gastos.api.dto.PrevisaoSaqueResponse;
import dev.Pedro.controle_gastos.domain.entity.Investimento;
import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static dev.Pedro.controle_gastos.api.service.calculo.RendimentoCalculator.*;
import static dev.Pedro.controle_gastos.api.service.calculo.TributacaoCalculator.calcularIOF;
import static dev.Pedro.controle_gastos.api.service.calculo.TributacaoCalculator.calcularIR;

public class PrevisaoSaqueCalculator {

    //Esse metodo precisou de rounding em cada operação pois deu erro de lenght
    public static PrevisaoSaqueResponse calcularInformacoesSaque(Investimento investimento) {

        if (investimento.getCategoria() == CategoriaInvestimento.OUTROS) {

            BigDecimal valor = investimento.getValorPosSaque().setScale(2, RoundingMode.HALF_EVEN);

            return new PrevisaoSaqueResponse(
                    valor,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO, valor
            );
        }

        LocalDate dataReferencia = dataReferencia(investimento.getData(),investimento.getUltimoSaque());
        long diasCorridos = calcularDiasCorridos(dataReferencia, LocalDate.now());

        BigDecimal valorBruto = valorBrutoFinal(investimento).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal rendimento = valorBruto.subtract(investimento.getValorPosSaque());

        BigDecimal iof = calcularIOF(rendimento, dataReferencia).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal rendimentoLiquidoIOF = rendimento.subtract(iof);

        BigDecimal ir = calcularIR(rendimentoLiquidoIOF, investimento.isIsentoIR(),diasCorridos).setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal valorDisponivel = investimento.getValorPosSaque()
                .add(rendimentoLiquidoIOF)
                .subtract(ir)
                .setScale(2, RoundingMode.HALF_EVEN);

        return new PrevisaoSaqueResponse(
                valorBruto,
                iof,
                ir,
                valorDisponivel
        );

    }
    public static BigDecimal valorDisponivelSaque(Investimento investimento) {

        return calcularInformacoesSaque(investimento).valorDisponivel();

    }


}
