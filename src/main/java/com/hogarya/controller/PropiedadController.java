package com.hogarya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.hogarya.entity.Propiedad;
import com.hogarya.entity.Persona;
import com.hogarya.service.PropiedadService;
import com.hogarya.service.PersonaService;

@Controller
public class PropiedadController {

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PersonaService personaService;

    @GetMapping("/propiedad")
    public String listar(Model model) {

        model.addAttribute("propiedad",
                propiedadService.listarActivas());

        return "propiedad/lista";
    }

    @GetMapping("/propiedad/nuevo") //CONTROLLER QUE ATIENDE PETICIONES DE LA URL
    public String nuevo(Model model) {

        model.addAttribute("propiedad", new Propiedad());
        model.addAttribute("propietarios", personaService.listarActivas());

        return "propiedad/formulario";
    }

    @PostMapping("/propiedad/guardar")
    public String guardar(@ModelAttribute Propiedad propiedad,
                          @RequestParam Long propietarioId) {

        Persona p = personaService.buscarPorId(propietarioId);
        propiedad.setPropietario(p);

        propiedadService.guardar(propiedad);

        return "redirect:/propiedad";
    }
}
