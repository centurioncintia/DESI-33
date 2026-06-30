package com.hogarya.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hogarya.entity.Contrato;
import com.hogarya.entity.ContratoEstadoHistorial;
import com.hogarya.entity.Persona;
import com.hogarya.entity.Propiedad;
import com.hogarya.enums.EstadoContrato;
import com.hogarya.repository.ContratoEstadoHistorialRepository;
import com.hogarya.repository.ContratoRepository;
import com.hogarya.repository.PersonaRepository;
import com.hogarya.repository.PropiedadRepository;

@Service
public class ContratoService {

    public static final String ESTADO_PROPIEDAD_DISPONIBLE = "DISPONIBLE";
    public static final String ESTADO_PROPIEDAD_ALQUILADA = "ALQUILADA";

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private ContratoEstadoHistorialRepository historialRepository;

    @Autowired
    private PropiedadRepository propiedadRepository;

    @Autowired
    private PersonaRepository personaRepository;

    //listado 

    public List<Contrato> listar(Long propiedadId, Long inquilinoId,
                                  EstadoContrato estado, LocalDate fechaInicio) {

        List<Contrato> contratos = contratoRepository.findByEliminadoFalse();

        return contratos.stream()
                .filter(c -> propiedadId == null
                        || (c.getPropiedad() != null && propiedadId.equals(c.getPropiedad().getId())))
                .filter(c -> inquilinoId == null
                        || (c.getInquilino() != null && inquilinoId.equals(c.getInquilino().getId())))
                .filter(c -> estado == null || estado.equals(c.getEstado()))
                .filter(c -> fechaInicio == null || fechaInicio.equals(c.getFechaInicio()))
                .toList();
    }

    public Contrato buscarPorId(Long id) {
        return contratoRepository.findById(id).orElse(null);
    }

    //crear contrato

    public String crear(Contrato contrato) {

        String errorDatos = validarDatosBasicos(contrato);
        if (errorDatos != null) {
            return errorDatos;
        }

        Propiedad propiedad = propiedadRepository.findById(contrato.getPropiedad().getId()).orElse(null);
        if (propiedad == null || Boolean.TRUE.equals(propiedad.getEliminado())) {
            return "La propiedad seleccionada no existe o fue eliminada";
        }

        Persona inquilino = personaRepository.findById(contrato.getInquilino().getId()).orElse(null);
        if (inquilino == null || Boolean.TRUE.equals(inquilino.getEliminado())) {
            return "El inquilino seleccionado no existe o fue eliminado";
        }

        if (contrato.getEstado() == EstadoContrato.ACTIVO) {
            String errorActivacion = validarPuedeActivarse(propiedad, null);
            if (errorActivacion != null) {
                return errorActivacion;
            }
        }

        contrato.setPropiedad(propiedad);
        contrato.setInquilino(inquilino);
        contrato.setEliminado(false);

        contratoRepository.save(contrato);

        registrarHistorial(contrato, null, contrato.getEstado());

        if (contrato.getEstado() == EstadoContrato.ACTIVO) {
            propiedad.setEstado(ESTADO_PROPIEDAD_ALQUILADA);
            propiedadRepository.save(propiedad);
        }

        return "OK";
    }

    
    //modificar

    public String actualizar(Long id, Contrato datos) {

        Contrato existente = buscarPorId(id);
        if (existente == null || Boolean.TRUE.equals(existente.getEliminado())) {
            return "El contrato no existe";
        }

        String errorDatos = validarDatosBasicos(datos);
        if (errorDatos != null) {
            return errorDatos;
        }

        Propiedad propiedad = propiedadRepository.findById(datos.getPropiedad().getId()).orElse(null);
        if (propiedad == null || Boolean.TRUE.equals(propiedad.getEliminado())) {
            return "La propiedad seleccionada no existe o fue eliminada";
        }

        Persona inquilino = personaRepository.findById(datos.getInquilino().getId()).orElse(null);
        if (inquilino == null || Boolean.TRUE.equals(inquilino.getEliminado())) {
            return "El inquilino seleccionado no existe o fue eliminado";
        }

        EstadoContrato estadoAnterior = existente.getEstado();
        EstadoContrato estadoNuevo = datos.getEstado();

        String errorTransicion = validarTransicionEstado(estadoAnterior, estadoNuevo);
        if (errorTransicion != null) {
            return errorTransicion;
        }

        boolean activando = estadoNuevo == EstadoContrato.ACTIVO && estadoAnterior != EstadoContrato.ACTIVO;
        boolean desactivando = estadoAnterior == EstadoContrato.ACTIVO
                && (estadoNuevo == EstadoContrato.FINALIZADO || estadoNuevo == EstadoContrato.RESCINDIDO);

        if (activando) {
            String errorActivacion = validarPuedeActivarse(propiedad, existente.getId());
            if (errorActivacion != null) {
                return errorActivacion;
            }
        }

        Propiedad propiedadAnterior = existente.getPropiedad();

        existente.setPropiedad(propiedad);
        existente.setInquilino(inquilino);
        existente.setFechaInicio(datos.getFechaInicio());
        existente.setImporteMensual(datos.getImporteMensual());
        existente.setDiaVencimiento(datos.getDiaVencimiento());
        existente.setDescripcion(datos.getDescripcion());
        existente.setEstado(estadoNuevo);

        contratoRepository.save(existente);

        if (estadoAnterior != estadoNuevo) {
            registrarHistorial(existente, estadoAnterior, estadoNuevo);
        }

        if (activando) {
            propiedad.setEstado(ESTADO_PROPIEDAD_ALQUILADA);
            propiedadRepository.save(propiedad);
        }

        if (desactivando) {
            // libera la propiedad que tenía el contrato antes de la actualización
            Propiedad aLiberar = propiedadAnterior != null ? propiedadAnterior : propiedad;
            aLiberar.setEstado(ESTADO_PROPIEDAD_DISPONIBLE);
            propiedadRepository.save(aLiberar);
        }

        return "OK";
    }

   //eliminar

    public String eliminar(Long id) {

        Contrato contrato = buscarPorId(id);

        if (contrato == null || Boolean.TRUE.equals(contrato.getEliminado())) {
            return "El contrato no existe";
        }

        if (contrato.getEstado() != EstadoContrato.BORRADOR) {
            return "Solo se pueden eliminar contratos en estado BORRADOR";
        }

        contrato.setEliminado(true);
        contratoRepository.save(contrato);

        return "OK";
    }

    //validaciones 



    private String validarDatosBasicos(Contrato c) {

        if (c.getPropiedad() == null || c.getPropiedad().getId() == null) {
            return "La propiedad es obligatoria";
        }

        if (c.getInquilino() == null || c.getInquilino().getId() == null) {
            return "El inquilino es obligatorio";
        }

        if (c.getFechaInicio() == null) {
            return "La fecha de inicio es obligatoria y debe ser válida";
        }

        if (c.getImporteMensual() == null || c.getImporteMensual().compareTo(BigDecimal.ZERO) <= 0) {
            return "El importe mensual debe ser un número positivo";
        }

        if (c.getDiaVencimiento() == null) {
            return "El día de vencimiento mensual es obligatorio y debe ser una fecha válida";
        }

        if (c.getDescripcion() == null || c.getDescripcion().isBlank()) {
            return "La descripción es obligatoria";
        }

        if (c.getEstado() == null) {
            return "El estado del contrato es obligatorio";
        }

        return null;
    }

    // valida que una propiedad pueda pasar a tener un contrato activo
    private String validarPuedeActivarse(Propiedad propiedad, Long contratoIdExcluido) {

        if (!ESTADO_PROPIEDAD_DISPONIBLE.equalsIgnoreCase(propiedad.getEstado())) {
            return "No se puede activar el contrato: la propiedad no está disponible";
        }

        List<Contrato> activos = contratoRepository
                .findByPropiedadIdAndEstadoAndEliminadoFalse(propiedad.getId(), EstadoContrato.ACTIVO);

        boolean existeOtroActivo = activos.stream()
                .anyMatch(c -> contratoIdExcluido == null || !c.getId().equals(contratoIdExcluido));

        if (existeOtroActivo) {
            return "La propiedad ya tiene un contrato activo";
        }

        return null;
    }

    // Verifica que los cambios de estado 
    //   BORRADOR -> ACTIVO
    //   ACTIVO -> FINALIZADO
    //   ACTIVO -> RESCINDIDO
    // No se permite volver de FINALIZADO/RESCINDIDO a ACTIVO 
    private String validarTransicionEstado(EstadoContrato anterior, EstadoContrato nuevo) {

        if (nuevo == null) {
            return "El estado del contrato es obligatorio";
        }

        if (anterior == nuevo) {
            return null; // sin cambio de estado, nada que validar
        }

        boolean transicionValida =
                (anterior == EstadoContrato.BORRADOR && nuevo == EstadoContrato.ACTIVO)
                || (anterior == EstadoContrato.ACTIVO && nuevo == EstadoContrato.FINALIZADO)
                || (anterior == EstadoContrato.ACTIVO && nuevo == EstadoContrato.RESCINDIDO);

        if (!transicionValida) {
            return "No se permite el cambio de estado de " + anterior + " a " + nuevo;
        }

        return null;
    }

    private void registrarHistorial(Contrato contrato, EstadoContrato anterior, EstadoContrato nuevo) {
        ContratoEstadoHistorial historial = new ContratoEstadoHistorial(
                contrato, anterior, nuevo, LocalDateTime.now());
        historialRepository.save(historial);
    }
}