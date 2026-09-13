package dev.Pedro.controle_gastos.api.dto.mapper;

import dev.Pedro.controle_gastos.api.dto.InvestimentoResponse;
import dev.Pedro.controle_gastos.api.dto.RendaFixaResponse;
import dev.Pedro.controle_gastos.domain.entity.Investimento;
import dev.Pedro.controle_gastos.domain.entity.RendaFixa;
import org.springframework.stereotype.Component;

import static dev.Pedro.controle_gastos.api.service.calculo.PrevisaoSaqueCalculator.calcularInformacoesSaque;
import static dev.Pedro.controle_gastos.api.service.calculo.RendimentoCalculator.calcularRendimento;

@Component
public class InvestimentoMapper {

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

            default -> throw new IllegalArgumentException(
                    "Tipo de investimento não suportado: "
                            + investimento.getClass().getSimpleName()
            );
        };
    }
}