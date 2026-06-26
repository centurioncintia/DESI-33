package com.hogarya.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hogarya.entity.Propiedad;
import com.hogarya.repository.PropiedadRepository;

@Service
public class PropiedadService {

    @Autowired
    private PropiedadRepository propiedadRepository;

    public List<Propiedad> listarActivas() {
        return propiedadRepository.findByEliminadoFalse();
    }

    public Propiedad guardar(Propiedad propiedad) {
        return propiedadRepository.save(propiedad);
    }

    public Propiedad buscarPorId(Long id) {
        return propiedadRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {

        Propiedad propiedad = buscarPorId(id);

        if (propiedad != null) {
            propiedad.setEliminado(true);
            propiedadRepository.save(propiedad);
        }
    }
}