
package com.hogarya.repository;

import com.hogarya.entity.HistorialEstadoPublicacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoPublicacionRepository extends JpaRepository<HistorialEstadoPublicacion, Long>{
    
    List<HistorialEstadoPublicacion> findByEliminadoFalse();
}
