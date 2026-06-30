package com.hogarya.entity;

import java.time.LocalDateTime;

import com.hogarya.enums.EstadoFactura;

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
@Table(name = "factura_estado_historial")
public class FacturaEstadoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estadoNuevo;

    private LocalDateTime fechaCambio;

    public FacturaEstadoHistorial() {
    }

    public FacturaEstadoHistorial(Factura factura, EstadoFactura estadoAnterior,
                                   EstadoFactura estadoNuevo, LocalDateTime fechaCambio) {
        this.factura = factura;
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

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public EstadoFactura getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoFactura estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoFactura getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoFactura estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}