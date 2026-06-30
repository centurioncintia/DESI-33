package com.hogarya.entity;

import java.time.LocalDateTime;

import com.hogarya.enums.EstadoContrato;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contrato_estado_historial")
public class ContratoEstadoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;

    @Enumerated(EnumType.STRING)
    private EstadoContrato estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoContrato estadoNuevo;

    private LocalDateTime fechaCambio;

    public ContratoEstadoHistorial() {
    }

    public ContratoEstadoHistorial(Contrato contrato, EstadoContrato estadoAnterior,
                                    EstadoContrato estadoNuevo, LocalDateTime fechaCambio) {
        this.contrato = contrato;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = fechaCambio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public EstadoContrato getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoContrato estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoContrato getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoContrato estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}