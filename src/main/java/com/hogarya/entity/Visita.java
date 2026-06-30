
package com.hogarya.entity;

import com.hogarya.enums.EstadoPublicacion;
import com.hogarya.enums.EstadoVisita;
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
public class Visita {
    
     @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
     
     @Enumerated (EnumType.STRING)
    private EstadoVisita estado;
     
     private LocalDate fechaHora;
     
     @ManyToOne
     @JoinColumn (name = "publicacio_id")
     private Publicacion publicacion;
     
      private boolean eliminado = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoVisita getEstado() {
        return estado;
    }

    public void setEstado(EstadoVisita estado) {
        this.estado = estado;
    }

    public LocalDate getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDate fechaHora) {
        this.fechaHora = fechaHora;
    }
     
     
}
