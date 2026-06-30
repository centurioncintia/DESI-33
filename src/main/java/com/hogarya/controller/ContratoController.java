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
import com.hogarya.entity.Persona;
import com.hogarya.entity.Propiedad;
import com.hogarya.enums.EstadoContrato;
import com.hogarya.service.ContratoService;
import com.hogarya.service.PersonaService;
import com.hogarya.service.PropiedadService;

@Controller
public class ContratoController {

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PersonaService personaService;

    // 3.4 Listado, con filtros opcionales
    @GetMapping("/contratos")
    public String listar(@RequestParam(required = false) Long propiedadId,
                          @RequestParam(required = false) Long inquilinoId,
                          @RequestParam(required = false) EstadoContrato estado,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                          Model model) {

        model.addAttribute("contrato",
                contratoService.listar(propiedadId, inquilinoId, estado, fechaInicio));

        model.addAttribute("propiedades", propiedadService.listar());
        model.addAttribute("inquilinos", personaService.listarActivas());
        model.addAttribute("estados", EstadoContrato.values());

        model.addAttribute("filtroPropiedadId", propiedadId);
        model.addAttribute("filtroInquilinoId", inquilinoId);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroFechaInicio", fechaInicio);

        return "contrato/lista";
    }

    // 3.1 Alta — formulario
    @GetMapping("/contratos/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("contrato", new Contrato());
        model.addAttribute("propiedades", propiedadService.listar());
        model.addAttribute("inquilinos", personaService.listarActivas());
        model.addAttribute("estados", EstadoContrato.values());
        model.addAttribute("modoEdicion", false);

        return "contrato/formulario";
    }

    // 3.1 Alta — guardar
    @PostMapping("/contratos/guardar")
    public String guardar(@ModelAttribute Contrato contrato,
                           @RequestParam Long propiedadId,
                           @RequestParam Long inquilinoId,
                           Model model) {

        Propiedad propiedad = propiedadService.buscarPorId(propiedadId);
        Persona inquilino = personaService.buscarPorId(inquilinoId);

        contrato.setPropiedad(propiedad);
        contrato.setInquilino(inquilino);

        String resultado = contratoService.crear(contrato);

        if (!"OK".equals(resultado)) {
            model.addAttribute("error", resultado);
            model.addAttribute("contrato", contrato);
            model.addAttribute("propiedades", propiedadService.listar());
            model.addAttribute("inquilinos", personaService.listarActivas());
            model.addAttribute("estados", EstadoContrato.values());
            model.addAttribute("modoEdicion", false);
            return "contrato/formulario";
        }

        return "redirect:/contratos";
    }

    // 3.3 Modificación — formulario
    @GetMapping("/contratos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Contrato contrato = contratoService.buscarPorId(id);

        model.addAttribute("contrato", contrato);
        model.addAttribute("propiedades", propiedadService.listar());
        model.addAttribute("inquilinos", personaService.listarActivas());
        model.addAttribute("estados", EstadoContrato.values());
        model.addAttribute("modoEdicion", true);

        return "contrato/formulario";
    }

    // 3.3 Modificación — guardar (el id es de solo lectura)
    @PostMapping("/contratos/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                              @ModelAttribute Contrato contrato,
                              @RequestParam Long propiedadId,
                              @RequestParam Long inquilinoId,
                              Model model) {

        Propiedad propiedad = propiedadService.buscarPorId(propiedadId);
        Persona inquilino = personaService.buscarPorId(inquilinoId);

        contrato.setPropiedad(propiedad);
        contrato.setInquilino(inquilino);

        String resultado = contratoService.actualizar(id, contrato);

        if (!"OK".equals(resultado)) {
            model.addAttribute("error", resultado);
            contrato.setId(id);
            model.addAttribute("contrato", contrato);
            model.addAttribute("propiedades", propiedadService.listar());
            model.addAttribute("inquilinos", personaService.listarActivas());
            model.addAttribute("estados", EstadoContrato.values());
            model.addAttribute("modoEdicion", true);
            return "contrato/formulario";
        }

        return "redirect:/contratos";
    }

    // 3.2 Eliminación lógica (solo en estado BORRADOR)
    @GetMapping("/contratos/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {

        String resultado = contratoService.eliminar(id);

        if (!"OK".equals(resultado)) {
            model.addAttribute("error", resultado);
        } else {
            model.addAttribute("success", "Contrato eliminado correctamente");
        }

        model.addAttribute("contratos", contratoService.listar(null, null, null, null));
        model.addAttribute("propiedades", propiedadService.listar());
        model.addAttribute("inquilinos", personaService.listarActivas());
        model.addAttribute("estados", EstadoContrato.values());

        return "contrato/lista";
    }
}