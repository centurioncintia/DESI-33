package com.hogarya.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hogarya.entity.Contrato;
import com.hogarya.entity.Factura;
import com.hogarya.entity.FacturaEstadoHistorial;
import com.hogarya.enums.EstadoContrato;
import com.hogarya.enums.EstadoFactura;
import com.hogarya.repository.ContratoRepository;
import com.hogarya.repository.FacturaEstadoHistorialRepository;
import com.hogarya.repository.FacturaRepository;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaEstadoHistorialRepository historialRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    //listado

    public List<Factura> listar(Long contratoId, Long propiedadId, Long inquilinoId,
                                 EstadoFactura estado, LocalDate fechaVencimientoDesde,
                                 LocalDate fechaVencimientoHasta) {

        List<Factura> facturas = facturaRepository.findByEliminadoFalse();

        return facturas.stream()
                .filter(f -> contratoId == null
                        || (f.getContrato() != null && contratoId.equals(f.getContrato().getId())))
                .filter(f -> propiedadId == null
                        || (f.getContrato() != null && f.getContrato().getPropiedad() != null
                            && propiedadId.equals(f.getContrato().getPropiedad().getId())))
                .filter(f -> inquilinoId == null
                        || (f.getContrato() != null && f.getContrato().getInquilino() != null
                            && inquilinoId.equals(f.getContrato().getInquilino().getId())))
                .filter(f -> estado == null || estado.equals(f.getEstado()))
                .filter(f -> fechaVencimientoDesde == null
                        || (f.getFechaVencimiento() != null
                            && !f.getFechaVencimiento().isBefore(fechaVencimientoDesde)))
                .filter(f -> fechaVencimientoHasta == null
                        || (f.getFechaVencimiento() != null
                            && !f.getFechaVencimiento().isAfter(fechaVencimientoHasta)))
                .toList();
    }

    public Factura buscarPorId(Long id) {
        return facturaRepository.findById(id).orElse(null);
    }

    // contratos activos 
    public List<Contrato> listarContratosDisponiblesParaFacturar() {
        return contratoRepository.findByEliminadoFalse().stream()
                .filter(c -> c.getEstado() == EstadoContrato.ACTIVO)
                .toList();
    }
    
    //crear
    public String crear(Factura factura) {

        if (factura.getContrato() == null || factura.getContrato().getId() == null) {
            return "El contrato es obligatorio";
        }

        Contrato contrato = contratoRepository.findById(factura.getContrato().getId()).orElse(null);

        String errorContrato = validarContratoParaFacturar(contrato);
        if (errorContrato != null) {
            return errorContrato;
        }

        String errorDatos = validarDatosBasicos(factura);
        if (errorDatos != null) {
            return errorDatos;
        }

        factura.setContrato(contrato);
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setFechaPago(null);
        factura.setMedioPago(null);
        factura.setImportePagado(null);
        factura.setInteresPagado(null);
        factura.setEliminado(false);

        facturaRepository.save(factura);

        registrarHistorial(factura, null, EstadoFactura.PENDIENTE);

        return "OK";
    }
    

    //modificar
    public String actualizar(Long id, Factura datos) {

        Factura existente = buscarPorId(id);
        if (existente == null || Boolean.TRUE.equals(existente.getEliminado())) {
            return "La factura no existe";
        }

        if (existente.getEstado() == EstadoFactura.ANULADA) {
            return "No se puede modificar una factura anulada";
        }

        if (existente.getEstado() == EstadoFactura.PAGADA) {
            return "No se puede modificar una factura pagada";
        }

        String errorDatos = validarDatosBasicos(datos);
        if (errorDatos != null) {
            return errorDatos;
        }

        EstadoFactura estadoAnterior = existente.getEstado();
        EstadoFactura estadoNuevo = datos.getEstado();

        String errorTransicion = validarTransicionEstado(estadoAnterior, estadoNuevo);
        if (errorTransicion != null) {
            return errorTransicion;
        }

        if (estadoNuevo == EstadoFactura.PAGADA) {

            String errorPago = validarDatosPago(datos);
            if (errorPago != null) {
                return errorPago;
            }

            existente.setFechaPago(datos.getFechaPago());
            existente.setMedioPago(datos.getMedioPago());
            existente.setImportePagado(datos.getImportePagado());
            existente.setInteresPagado(datos.getInteresPagado());

        } else {
            // si no es  pagada   los datos de pago deben estar vacíos
            existente.setFechaPago(null);
            existente.setMedioPago(null);
            existente.setImportePagado(null);
            existente.setInteresPagado(null);
        }

        // no se puede modificar

        existente.setConceptoFacturado(datos.getConceptoFacturado());
        existente.setFechaEmision(datos.getFechaEmision());
        existente.setFechaVencimiento(datos.getFechaVencimiento());
        existente.setImporte(datos.getImporte());
        existente.setEstado(estadoNuevo);

        facturaRepository.save(existente);

        if (estadoAnterior != estadoNuevo) {
            registrarHistorial(existente, estadoAnterior, estadoNuevo);
        }

        return "OK";
    }
    
    //eliminaar
    public String eliminar(Long id) {

        Factura factura = buscarPorId(id);

        if (factura == null || Boolean.TRUE.equals(factura.getEliminado())) {
            return "La factura no existe";
        }

        if (factura.getEstado() == EstadoFactura.PAGADA) {
            return "No se puede eliminar una factura pagada";
        }

        factura.setEliminado(true);
        facturaRepository.save(factura);

        return "OK";
    }

    
    //validaciones

    private String validarContratoParaFacturar(Contrato contrato) {

        if (contrato == null || Boolean.TRUE.equals(contrato.getEliminado())) {
            return "El contrato seleccionado no existe o fue eliminado";
        }

        if (contrato.getEstado() != EstadoContrato.ACTIVO) {
            return "No se puede crear una factura para un contrato finalizado, rescindido, eliminado o en borrador";
        }

        return null;
    }

    private String validarDatosBasicos(Factura f) {

        if (f.getConceptoFacturado() == null || f.getConceptoFacturado().isBlank()) {
            return "El concepto facturado es obligatorio";
        }

        if (f.getFechaEmision() == null) {
            return "La fecha de emisión es obligatoria y debe ser válida";
        }

        if (f.getFechaVencimiento() == null) {
            return "La fecha de vencimiento es obligatoria y debe ser válida";
        }

        if (f.getFechaVencimiento().isBefore(f.getFechaEmision())) {
            return "La fecha de vencimiento debe ser igual o posterior a la fecha de emisión";
        }

        if (f.getImporte() == null || f.getImporte().compareTo(BigDecimal.ZERO) <= 0) {
            return "El importe debe ser un número positivo";
        }

        return null;
    }
    private String validarDatosPago(Factura f) {

        if (f.getFechaPago() == null) {
            return "La fecha de pago es obligatoria para registrar el pago";
        }

        if (f.getMedioPago() == null) {
            return "El medio de pago es obligatorio para registrar el pago";
        }

        if (f.getImportePagado() == null || f.getImportePagado().compareTo(BigDecimal.ZERO) <= 0) {
            return "El importe pagado debe ser un número positivo";
        }

        if (f.getInteresPagado() != null && f.getInteresPagado().compareTo(BigDecimal.ZERO) < 0) {
            return "El interés pagado no puede ser negativo";
        }

        return null;
    }

    //verifica cambios estado
    private String validarTransicionEstado(EstadoFactura anterior, EstadoFactura nuevo) {

        if (nuevo == null) {
            return "El estado de la factura es obligatorio";
        }

        if (anterior == nuevo) {
            return null; // sin cambio de estado, nada que validar
        }

        boolean transicionValida =
                (anterior == EstadoFactura.PENDIENTE && nuevo == EstadoFactura.PAGADA)
                || (anterior == EstadoFactura.PENDIENTE && nuevo == EstadoFactura.VENCIDA)
                || (anterior == EstadoFactura.VENCIDA && nuevo == EstadoFactura.PAGADA)
                || (anterior == EstadoFactura.PENDIENTE && nuevo == EstadoFactura.ANULADA);

        if (!transicionValida) {
            return "No se permite el cambio de estado de " + anterior + " a " + nuevo;
        }

        return null;
    }

    private void registrarHistorial(Factura factura, EstadoFactura anterior, EstadoFactura nuevo) {
        FacturaEstadoHistorial historial = new FacturaEstadoHistorial(
                factura, anterior, nuevo, LocalDateTime.now());
        historialRepository.save(historial);
    }
}