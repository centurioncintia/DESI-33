package com.hogarya.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hogarya.entity.Contrato;
import com.hogarya.entity.Factura;
import com.hogarya.enums.EstadoFactura;
import com.hogarya.enums.MedioPago;
import com.hogarya.service.ContratoService;
import com.hogarya.service.FacturaService;
import com.hogarya.service.PersonaService;
import com.hogarya.service.PropiedadService;

@Controller
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PersonaService personaService;

    // listado con filtros
    @GetMapping("/facturas")
    public String listar(@RequestParam(required = false) Long contratoId,
                          @RequestParam(required = false) Long propiedadId,
                          @RequestParam(required = false) Long inquilinoId,
                          @RequestParam(required = false) EstadoFactura estado,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimientoDesde,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimientoHasta,
                          Model model) {

        model.addAttribute("facturas",
                facturaService.listar(contratoId, propiedadId, inquilinoId, estado,
                        fechaVencimientoDesde, fechaVencimientoHasta));

        model.addAttribute("contratos", contratoService.listar(null, null, null, null));
        model.addAttribute("propiedades", propiedadService.listar());
        model.addAttribute("inquilinos", personaService.listarActivas());
        model.addAttribute("estados", EstadoFactura.values());

        model.addAttribute("filtroContratoId", contratoId);
        model.addAttribute("filtroPropiedadId", propiedadId);
        model.addAttribute("filtroInquilinoId", inquilinoId);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroFechaVencimientoDesde", fechaVencimientoDesde);
        model.addAttribute("filtroFechaVencimientoHasta", fechaVencimientoHasta);

        return "factura/lista";
    }

    // nuevo formulario
    @GetMapping("/facturas/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("factura", new Factura());
        model.addAttribute("contratosDisponibles", facturaService.listarContratosDisponiblesParaFacturar());
        model.addAttribute("modoEdicion", false);

        return "factura/formulario";
    }

    // guardar
    @PostMapping("/facturas/guardar")
    public String guardar(@ModelAttribute Factura factura,
                           @RequestParam Long contratoId,
                           Model model) {

        Contrato contrato = contratoService.buscarPorId(contratoId);
        factura.setContrato(contrato);

        String resultado = facturaService.crear(factura);

        if (!"OK".equals(resultado)) {
            model.addAttribute("error", resultado);
            model.addAttribute("factura", factura);
            model.addAttribute("contratosDisponibles", facturaService.listarContratosDisponiblesParaFacturar());
            model.addAttribute("modoEdicion", false);
            return "factura/formulario";
        }

        return "redirect:/facturas";
    }
    // modificar
    @GetMapping("/facturas/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Factura factura = facturaService.buscarPorId(id);

        model.addAttribute("factura", factura);
        model.addAttribute("estados", EstadoFactura.values());
        model.addAttribute("medios", MedioPago.values());
        model.addAttribute("modoEdicion", true);

        return "factura/formulario";
    }

    // modifcar guardar sin id
    @PostMapping("/facturas/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                              @ModelAttribute Factura factura,
                              Model model) {

        String resultado = facturaService.actualizar(id, factura);

        if (!"OK".equals(resultado)) {
            model.addAttribute("error", resultado);
            factura.setId(id);
            model.addAttribute("factura", factura);
            model.addAttribute("estados", EstadoFactura.values());
            model.addAttribute("medios", MedioPago.values());
            model.addAttribute("modoEdicion", true);
            return "factura/formulario";
        }

        return "redirect:/facturas";
    }

    // eliminar
    @GetMapping("/facturas/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {

        String resultado = facturaService.eliminar(id);

        if (!"OK".equals(resultado)) {
            model.addAttribute("error", resultado);
        } else {
            model.addAttribute("success", "Factura eliminada correctamente");
        }

        model.addAttribute("facturas", facturaService.listar(null, null, null, null, null, null));
        model.addAttribute("contratos", contratoService.listar(null, null, null, null));
        model.addAttribute("propiedades", propiedadService.listar());
        model.addAttribute("inquilinos", personaService.listarActivas());
        model.addAttribute("estados", EstadoFactura.values());

        return "factura/lista";
    }
}