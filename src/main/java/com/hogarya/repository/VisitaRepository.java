
package com.hogarya.repository;

import com.hogarya.entity.Visita;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitaRepository extends JpaRepository<Visita, Long>{
    
    List<Visita> findByEliminadoFalse();
}
