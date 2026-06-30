package com.hogarya.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hogarya.entity.ContratoEstadoHistorial;

@Repository
public interface ContratoEstadoHistorialRepository extends JpaRepository<ContratoEstadoHistorial, Long> {
}