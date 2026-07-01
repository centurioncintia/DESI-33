
package com.hogarya.entity;

import com.hogarya.enums.EstadoPublicacion;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class HistorialEstadoPublicacion {
    
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated (EnumType.STRING)
    private EstadoPublicacion estado;
    
    private LocalDate fechaHora;
    
    @ManyToOne
    @JoinColumn (name = "publicacion_id")
    private Publicacion publicacion;
    
    private boolean eliminado = false;
}
