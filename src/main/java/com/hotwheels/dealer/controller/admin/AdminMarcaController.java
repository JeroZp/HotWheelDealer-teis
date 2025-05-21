package com.hotwheels.dealer.controller.admin;

import com.hotwheels.dealer.entity.Marca;
import com.hotwheels.dealer.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/marcas")
public class AdminMarcaController {

    @Autowired
    private MarcaService marcaService;

    @GetMapping
    public String listar(Model model) {
        List<Marca> marcas = marcaService.findAll();
        model.addAttribute("marcas", marcas);
        model.addAttribute("titulo", "Gestión de Marcas");
        return "admin/marcas/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("marca", new Marca());
        model.addAttribute("titulo", "Nueva Marca");
        return "admin/marcas/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Marca marca, BindingResult result,
                          RedirectAttributes flash, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", marca.getId() != null ? "Editar Marca" : "Nueva Marca");
            return "admin/marcas/form";
        }

        marcaService.save(marca);
        flash.addFlashAttribute("success", "Marca guardada con éxito");
        return "redirect:/admin/marcas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Optional<Marca> marcaOpt = marcaService.findById(id);

        if (marcaOpt.isPresent()) {
            model.addAttribute("marca", marcaOpt.get());
            model.addAttribute("titulo", "Editar Marca");
            return "admin/marcas/form";
        }

        flash.addFlashAttribute("error", "La marca no existe");
        return "redirect:/admin/marcas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        Optional<Marca> marcaOpt = marcaService.findById(id);

        if (marcaOpt.isPresent()) {
            // Solo permitimos eliminar si no tiene modelos asociados
            Marca marca = marcaOpt.get();
            if (marca.getModelos() == null || marca.getModelos().isEmpty()) {
                marcaService.eliminar(id);
                flash.addFlashAttribute("success", "Marca eliminada con éxito");
            } else {
                flash.addFlashAttribute("error", "No se puede eliminar la marca porque tiene modelos asociados");
            }
        } else {
            flash.addFlashAttribute("error", "La marca no existe");
        }

        return "redirect:/admin/marcas";
    }
}