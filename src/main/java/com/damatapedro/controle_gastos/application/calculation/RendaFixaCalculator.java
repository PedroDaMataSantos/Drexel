package com.damatapedro.controle_gastos.application.calculation;

import com.damatapedro.controle_gastos.application.dto.PrevisaoResgateParcialResponse;
import com.damatapedro.controle_gastos.domain.entity.RendaFixa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static com.damatapedro.controle_gastos.application.calculation.RendimentoCalculator.*;
import static com.damatapedro.controle_gastos.application.calculation.TributacaoCalculator.calcularIOF;
import static com.damatapedro.controle_gastos.application.calculation.TributacaoCalculator.calcularIR;

public class RendaFixaCalculator {

    //Esse metodo precisou de rounding em cada operação pois deu erro de lenght
    public static PrevisaoResgateParcialResponse calcularInformacoesSaque(RendaFixa rendaFixa) {

        LocalDate dataReferencia = dataReferencia(rendaFixa.getData(),rendaFixa.getUltimoSaque());
        long diasCorridos = calcularDiasCorridos(dataReferencia, LocalDate.now());

        BigDecimal valorBruto = valorBrutoFinal(rendaFixa).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal rendimento = calcularRendimento(rendaFixa);

        BigDecimal iof = calcularIOF(rendimento, dataReferencia).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal rendimentoLiquidoIOF = rendimento.subtract(iof);

        BigDecimal ir = calcularIR(rendimentoLiquidoIOF, rendaFixa.isIsentoIR(),diasCorridos).setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal valorDisponivel = valorBruto
                .subtract(iof)
                .subtract(ir)
                .setScale(2, RoundingMode.HALF_EVEN);

        return new PrevisaoResgateParcialResponse(
                valorBruto,
                iof,
                ir,
                valorDisponivel
        );

    }
    public static BigDecimal valorDisponivelSaque(RendaFixa rendaFixa) {

        return calcularInformacoesSaque(rendaFixa).valorDisponivel();

    }




}
