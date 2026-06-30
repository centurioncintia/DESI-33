package com.hogarya.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hogarya.entity.FacturaEstadoHistorial;

@Repository
public interface FacturaEstadoHistorialRepository extends JpaRepository<FacturaEstadoHistorial, Long> {
}