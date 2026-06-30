package com.hogarya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hogarya.entity.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    List<Factura> findByEliminadoFalse();
}