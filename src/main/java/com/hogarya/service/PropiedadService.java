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

    // Lista solo las propiedades NO eliminadas
    public List<Propiedad> listar() {
        return propiedadRepository.findByEliminadoFalse();
    }

    // Guarda una propiedad
    public Propiedad guardar(Propiedad propiedad) {
    	
    	 propiedad.setEliminado(false);
        return propiedadRepository.save(propiedad);
    }

    // Busca una propiedad por ID
    public Propiedad buscarPorId(Long id) {
        return propiedadRepository.findById(id).orElse(null);
    }


    public String eliminar(Long id) {

        Propiedad propiedad = buscarPorId(id);

        if (propiedad == null) {
            return "No se encontró la propiedad";
        }

        
        if ("ALQUILADA".equalsIgnoreCase(propiedad.getEstado()) ||
            "RESERVADA".equalsIgnoreCase(propiedad.getEstado())) {

            return "No se puede eliminar: la propiedad tiene un contrato activo vigente";
        }

        propiedad.setEliminado(true);
        propiedadRepository.save(propiedad);

        return "OK";
    }
   
    public String guardarConValidaciones(Propiedad p) {

        // 1. duplicado por dirección (solo activas)
        List<Propiedad> existentes = propiedadRepository.findByEliminadoFalse();

        for (Propiedad e : existentes) {
            if (!e.getId().equals(p.getId()) &&
                e.getDireccion().equalsIgnoreCase(p.getDireccion())) {
                return "Ya existe una propiedad activa con esa dirección";
            }
        }

        // 2. validar contrato activo (simulado con estado)
        Propiedad actual = null;

        if (p.getId() != null) {
            actual = buscarPorId(p.getId());
        }

        if (actual != null) {

            boolean tieneContratoActivo =
                    "ALQUILADA".equalsIgnoreCase(actual.getEstado());

            // no permitir volver a DISPONIBLE si tiene contrato activo
            if (tieneContratoActivo &&
                "DISPONIBLE".equalsIgnoreCase(p.getEstado())) {

                return "No se puede cambiar a DISPONIBLE: tiene contrato activo";
            }
        }

        // guardar normal
        propiedadRepository.save(p);
        return "OK";
    }
    
    
}