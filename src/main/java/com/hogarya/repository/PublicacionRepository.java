
package com.hogarya.repository;

import com.hogarya.entity.Publicacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface PublicacionRepository extends JpaRepository<Publicacion, Long>{
    
    List<Publicacion> findByEliminadoFalse();
}
