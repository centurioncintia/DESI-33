package com.hogarya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hogarya.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    List<Persona> findByEliminadoFalse();

    boolean existsByDni(String dni);

}
