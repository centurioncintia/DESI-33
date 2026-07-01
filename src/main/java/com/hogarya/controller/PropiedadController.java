package com.hogarya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.hogarya.entity.Persona;
import com.hogarya.entity.Propiedad;
import com.hogarya.service.PersonaService;
import com.hogarya.service.PropiedadService;

@Controller
public class PropiedadController {

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PersonaService personaService;

    @GetMapping("/propiedades")
    public String listar(Model model) {


        model.addAttribute("propiedades",
                propiedadService.listar());

        System.out.println("Cantidad de propiedades: " + propiedadService.listar().size());

        model.addAttribute("propiedades", propiedadService.listar());


        return "propiedad/lista";
    }
    @GetMapping("/propiedades/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("propiedades", new Propiedad());
        model.addAttribute("propietarios", personaService.listarActivas());

        return "propiedad/formulario";
    }

    @PostMapping("/propiedades/guardar")
    public String guardar(@ModelAttribute Propiedad propiedad,
                          @RequestParam Long propietarioId,
                          Model model) {

        Persona propietario = personaService.buscarPorId(propietarioId);
        propiedad.setPropietario(propietario);

        String resultado = propiedadService.guardarConValidaciones(propiedad);

        if (!resultado.equals("OK")) {
            model.addAttribute("error", resultado);
            model.addAttribute("propietarios", personaService.listarActivas());
            return "propiedad/formulario";
        }

        return "redirect:/propiedades";
    }

    
    @GetMapping("/propiedades/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {

        String resultado = propiedadService.eliminar(id);

        if (!resultado.equals("OK")) {
            model.addAttribute("error", resultado);
        } else {
            model.addAttribute("success", "Propiedad eliminada correctamente");
        }

        model.addAttribute("propiedades", propiedadService.listar());

        return "propiedad/lista";
    }
    
    @GetMapping("/propiedades/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Propiedad propiedad = propiedadService.buscarPorId(id);

        model.addAttribute("propiedades", propiedad);
        model.addAttribute("propietarios", personaService.listarActivas());

        return "propiedad/formulario";
    }
    
    
    
    
    
}
