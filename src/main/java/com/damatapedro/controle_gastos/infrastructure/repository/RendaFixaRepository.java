package com.damatapedro.controle_gastos.infrastructure.repository;

import com.damatapedro.controle_gastos.domain.entity.RendaFixa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RendaFixaRepository extends JpaRepository<RendaFixa, Long> {

    List<RendaFixa> findByDataBetween(LocalDate inicio, LocalDate fim);

}
