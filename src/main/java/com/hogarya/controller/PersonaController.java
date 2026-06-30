package com.hogarya.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.hogarya.entity.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hogarya.service.PersonaService;

@Controller
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @GetMapping("/persona")
    public String listar(Model model) {

        model.addAttribute("persona",
                personaService.listarActivas());

        return "persona/lista";
    }

    @GetMapping("/persona/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("persona", new Persona());

        return "persona/formulario";
    }

    @PostMapping("/persona/guardar")
    public String guardar(@ModelAttribute Persona persona) {

        personaService.guardar(persona);

        return "redirect:/persona";
    }
}