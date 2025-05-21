package com.hotwheels.dealer.controller.admin;

import com.hotwheels.dealer.entity.Vehiculo;
import com.hotwheels.dealer.service.VehiculoService;
import com.hotwheels.dealer.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/vehiculos")
public class AdminVehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private ModeloService modeloService;

    @GetMapping
    public String listar(Model model) {
        List<Vehiculo> vehiculos = vehiculoService.findAll();
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("titulo", "Gestión de Vehículos");
        return "admin/vehiculos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("vehiculo", new Vehiculo());
        model.addAttribute("modelos", modeloService.findAllActivos());
        model.addAttribute("tiposTransmision", Vehiculo.TipoTransmision.values());
        model.addAttribute("tiposCombustible", Vehiculo.TipoCombustible.values());
        model.addAttribute("titulo", "Nuevo Vehículo");
        return "admin/vehiculos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Vehiculo vehiculo, BindingResult result,
                          RedirectAttributes flash, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("modelos", modeloService.findAllActivos());
            model.addAttribute("tiposTransmision", Vehiculo.TipoTransmision.values());
            model.addAttribute("tiposCombustible", Vehiculo.TipoCombustible.values());
            model.addAttribute("titulo", vehiculo.getId() != null ? "Editar Vehículo" : "Nuevo Vehículo");
            return "admin/vehiculos/form";
        }

        vehiculoService.save(vehiculo);
        flash.addFlashAttribute("success", "Vehículo guardado con éxito");
        return "redirect:/admin/vehiculos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Optional<Vehiculo> vehiculoOpt = vehiculoService.findById(id);

        if (vehiculoOpt.isPresent()) {
            model.addAttribute("vehiculo", vehiculoOpt.get());
            model.addAttribute("modelos", modeloService.findAllActivos());
            model.addAttribute("tiposTransmision", Vehiculo.TipoTransmision.values());
            model.addAttribute("tiposCombustible", Vehiculo.TipoCombustible.values());
            model.addAttribute("titulo", "Editar Vehículo");
            return "admin/vehiculos/form";
        }

        flash.addFlashAttribute("error", "El vehículo no existe");
        return "redirect:/admin/vehiculos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        vehiculoService.eliminar(id);
        flash.addFlashAttribute("success", "Vehículo eliminado con éxito");
        return "redirect:/admin/vehiculos";
    }
}