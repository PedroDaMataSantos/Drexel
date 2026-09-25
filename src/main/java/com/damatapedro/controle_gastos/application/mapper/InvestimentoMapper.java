package com.damatapedro.controle_gastos.application.mapper;

import com.damatapedro.controle_gastos.application.dto.InvestimentoResponse;
import com.damatapedro.controle_gastos.application.dto.RendaFixaResponse;
import com.damatapedro.controle_gastos.application.exception.TipoInvestimentoInvalidoException;
import com.damatapedro.controle_gastos.domain.entity.Investimento;
import com.damatapedro.controle_gastos.domain.entity.RendaFixa;
import org.springframework.stereotype.Component;

import static com.damatapedro.controle_gastos.application.calculation.RendaFixaCalculator.calcularInformacoesSaque;
import static com.damatapedro.controle_gastos.application.calculation.RendimentoCalculator.calcularRendimento;

@Component
public class    InvestimentoMapper {

    public InvestimentoResponse toResponse(Investimento investimento) {
        return switch (investimento) {
            case RendaFixa rf -> new RendaFixaResponse(
                    rf.getId(),
                    rf.getDescricao(),
                    rf.getValorAplicado(),
                    rf.getSaldoAtual(),
                    rf.getData(),
                    rf.getCategoria(),
                    rf.isAporte(),
                    rf.isIsentoIR(),
                    rf.getTaxaJuros(),
                    rf.getPeriodicidadeTaxa(),
                    rf.getUltimoSaque(),
                    calcularRendimento(rf),
                    calcularInformacoesSaque(rf).valorBruto()
            );

            //case Renda Variavel quando disponivel

            default -> throw new TipoInvestimentoInvalidoException(
                    "Tipo de investimento não suportado: "
                            + investimento.getClass().getSimpleName()
            );
        };
    }
}