package com.damatapedro.controle_gastos.infrastructure.repository;

import com.damatapedro.controle_gastos.domain.entity.Registro;
import com.damatapedro.controle_gastos.domain.enumeration.CategoriaRegistro;
import com.damatapedro.controle_gastos.domain.enumeration.TipoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    List<Registro> findByDataBetween(LocalDate inicio, LocalDate fim);

    List<Registro> findByTipoRegistro(TipoRegistro tipo);

    List<Registro> findByCategoria(CategoriaRegistro categoria);

    List<Registro> findByTipoRegistroAndDataBetween(TipoRegistro tipoRegistro, LocalDate inicio, LocalDate fim);

    List<Registro> findByCategoriaAndDataBetween(CategoriaRegistro categoria, LocalDate inicio, LocalDate fim);

    List<Registro> findByDescricaoContainingIgnoreCase(String descricao);


}
