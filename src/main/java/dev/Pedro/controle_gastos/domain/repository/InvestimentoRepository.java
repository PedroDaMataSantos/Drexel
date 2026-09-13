package dev.Pedro.controle_gastos.domain.repository;

import dev.Pedro.controle_gastos.domain.entity.Investimento;
import dev.Pedro.controle_gastos.enums.CategoriaInvestimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {

    List<Investimento> findByCategoria(CategoriaInvestimento categoria);

    List<Investimento> findByDataBetween(LocalDate inicio, LocalDate fim);

    List <Investimento> findByisAporte(boolean isAPorte);

    List <Investimento> findByisAporteAndDataBetween(boolean isAporte, LocalDate inicio, LocalDate fim);



}
