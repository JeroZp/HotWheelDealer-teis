package com.hotwheels.dealer.controller.admin;

import com.hotwheels.dealer.entity.Modelo;
import com.hotwheels.dealer.service.ModeloService;
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
@RequestMapping("/admin/modelos")
public class AdminModeloController {

    @Autowired
    private ModeloService modeloService;

    @Autowired
    private MarcaService marcaService;

    @GetMapping
    public String listar(Model model) {
        List<Modelo> modelos = modeloService.findAll();
        model.addAttribute("modelos", modelos);
        model.addAttribute("titulo", "Gestión de Modelos");
        return "admin/modelos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("modelo", new Modelo());
        model.addAttribute("marcas", marcaService.findAllActivas());
        model.addAttribute("titulo", "Nuevo Modelo");
        return "admin/modelos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Modelo modelo, BindingResult result,
                          RedirectAttributes flash, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("marcas", marcaService.findAllActivas());
            model.addAttribute("titulo", modelo.getId() != null ? "Editar Modelo" : "Nuevo Modelo");
            return "admin/modelos/form";
        }

        modeloService.save(modelo);
        flash.addFlashAttribute("success", "Modelo guardado con éxito");
        return "redirect:/admin/modelos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Optional<Modelo> modeloOpt = modeloService.findById(id);

        if (modeloOpt.isPresent()) {
            model.addAttribute("modelo", modeloOpt.get());
            model.addAttribute("marcas", marcaService.findAllActivas());
            model.addAttribute("titulo", "Editar Modelo");
            return "admin/modelos/form";
        }

        flash.addFlashAttribute("error", "El modelo no existe");
        return "redirect:/admin/modelos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        Optional<Modelo> modeloOpt = modeloService.findById(id);

        if (modeloOpt.isPresent()) {
            // Solo permitimos eliminar si no tiene vehículos asociados
            Modelo modelo = modeloOpt.get();
            if (modelo.getVehiculos() == null || modelo.getVehiculos().isEmpty()) {
                modeloService.eliminar(id);
                flash.addFlashAttribute("success", "Modelo eliminado con éxito");
            } else {
                flash.addFlashAttribute("error", "No se puede eliminar el modelo porque tiene vehículos asociados");
            }
        } else {
            flash.addFlashAttribute("error", "El modelo no existe");
        }

        return "redirect:/admin/modelos";
    }
}