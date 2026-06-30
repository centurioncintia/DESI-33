package com.hogarya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hogarya.entity.Contrato;
import com.hogarya.enums.EstadoContrato;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByEliminadoFalse();

    // contratos activos de una propiedad -valida que no haya mas de uno
    List<Contrato> findByPropiedadIdAndEstadoAndEliminadoFalse(Long propiedadId, EstadoContrato estado);
}