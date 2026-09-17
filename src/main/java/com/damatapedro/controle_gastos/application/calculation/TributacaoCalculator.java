package com.damatapedro.controle_gastos.application.calculation;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.damatapedro.controle_gastos.application.calculation.RendimentoCalculator.calcularDiasCorridos;


public class TributacaoCalculator {

    public static BigDecimal calcularIOF(BigDecimal rendimentoBruto, LocalDate dataAplicacao) {


        int[] tabelaIOF = {96, 93, 90, 86, 83, 80, 76, 73, 70, 66, 63, 60, 56, 53, 50, 46, 43, 40, 36, 33, 30, 26, 23, 20, 16, 13, 10, 6, 3};
        BigDecimal iof = BigDecimal.ZERO;
        BigDecimal aliquota;
        Long diasCorridos = calcularDiasCorridos(dataAplicacao, LocalDate.now());

        if (diasCorridos >= 30) {
            aliquota = BigDecimal.ZERO;
        } else if (diasCorridos <= 0) {
            aliquota = BigDecimal.valueOf(tabelaIOF[0]);
        } else {
            aliquota = BigDecimal.valueOf(tabelaIOF[diasCorridos.intValue() - 1]);
        }

        if (rendimentoBruto.compareTo(BigDecimal.ZERO) > 0) {

            iof = rendimentoBruto.multiply(aliquota.divide(BigDecimal.valueOf(100)));
        }

        return iof;
    }


    public static BigDecimal calcularIR(BigDecimal rendimentoLiquidoIOF, boolean isIsentoIR, long diasCorridos) {

        if (isIsentoIR) {
            return BigDecimal.ZERO;
        }

        BigDecimal aliquota;

        if (diasCorridos <= 180) {
            aliquota = BigDecimal.valueOf(22.5);
        } else if (diasCorridos <= 360) {
            aliquota = BigDecimal.valueOf(20);
        } else if (diasCorridos <= 720) {
            aliquota = BigDecimal.valueOf(17.5);
        } else {
            aliquota = BigDecimal.valueOf(15);
        }

        return rendimentoLiquidoIOF.multiply(aliquota.divide(BigDecimal.valueOf(100)));
    }


}
