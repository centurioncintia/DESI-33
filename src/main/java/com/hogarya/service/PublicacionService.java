
package com.hogarya.service;

import com.hogarya.entity.Publicacion;
import com.hogarya.repository.PublicacionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicacionService {
    
    @Autowired
    private PublicacionRepository publicacionRepository;
    
    public List<Publicacion> listarActivas(){
        return publicacionRepository.findByEliminadoFalse();
    }
    
    public Publicacion guardar(Publicacion publicacion){
        return publicacionRepository.save(publicacion);
    }
    
    public Publicacion buscarPorId(Long id){
        return publicacionRepository.findById(id).orElse(null);
    }
    
    public void eliminar(Long id) {

        Publicacion publicacion = buscarPorId(id);

        if (publicacion != null) {
            publicacion.setEliminado(true);
            publicacionRepository.save(publicacion);
        }
    }
}
