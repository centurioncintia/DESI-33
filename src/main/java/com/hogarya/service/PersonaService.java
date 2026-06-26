package com.hogarya.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hogarya.entity.Persona;
import com.hogarya.repository.PersonaRepository;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    public List<Persona> listarActivas() {
        return personaRepository.findByEliminadoFalse();
    }

    public Persona guardar(Persona persona) {
        return personaRepository.save(persona);
    }

    public Persona buscarPorId(Long id) {
        return personaRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {

        Persona persona = buscarPorId(id);

        if (persona != null) {
            persona.setEliminado(true);
            personaRepository.save(persona);
        }
    }

    public boolean existeDni(String dni) {
        return personaRepository.existsByDni(dni);
    }
}