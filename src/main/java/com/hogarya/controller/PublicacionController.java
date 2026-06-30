
package com.hogarya.controller;

import com.hogarya.entity.Propiedad;
import com.hogarya.entity.Publicacion;
import com.hogarya.service.PropiedadService;
import com.hogarya.service.PublicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PublicacionController {
    
    @Autowired
    private PropiedadService propiedadService;
    
    @Autowired
    private PublicacionService publicacionService;
    
    @GetMapping("/publicaciones")
    public String listar(Model model){
        
        model.addAttribute("publicacion", publicacionService.listarActivas());
        
        return "publicacion/lista";
    }
    
    @GetMapping("/publicaciones/nuevo")
    public String nuevo(Model model){
        
        model.addAttribute("publicacion", new Publicacion());
        model.addAttribute("propiedades", propiedadService.listar());
        
        return "publicacion/formulario";
    }
    
    @PostMapping("/publicaciones/guardar")
    public String guardar(@ModelAttribute Publicacion publicacion,
                          @RequestParam Long propiedadId){
        Propiedad p = propiedadService.buscarPorId(propiedadId);
        publicacion.setPropiedad(p);
        
        publicacionService.guardar(publicacion);
        
        return "redirect:/publicaciones";
    }
    
    @GetMapping("/publicaciones/eliminar/{id}")
    public String eliminar(@PathVariable Long id){
        
        publicacionService.eliminar(id);
        
        return "redirect:/publicaciones";
    }
    
    @GetMapping("/publicaciones/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

    Publicacion publicacion = publicacionService.buscarPorId(id);

    model.addAttribute("publicacion", publicacion);
    model.addAttribute("propiedades", propiedadService.listar());


    return "publicacion/formulario";
}
}
