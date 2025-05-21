package com.hotwheels.dealer.controller.web;

import com.hotwheels.dealer.entity.Vehiculo;
import com.hotwheels.dealer.entity.Marca;
import com.hotwheels.dealer.entity.Modelo;
import com.hotwheels.dealer.service.VehiculoService;
import com.hotwheels.dealer.service.MarcaService;
import com.hotwheels.dealer.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private ModeloService modeloService;

    @GetMapping("/{id}")
    public String detalleVehiculo(@PathVariable Long id, Model model) {
        Optional<Vehiculo> vehiculoOpt = vehiculoService.findById(id);

        if (vehiculoOpt.isPresent()) {
            Vehiculo vehiculo = vehiculoOpt.get();

            // Incrementa contador de visualizaciones
            vehiculoService.incrementarVisualizaciones(id);

            // Obtener vehiculos similares (misma marca)
            List<Vehiculo> similares = vehiculoService.findByMarca(vehiculo.getModelo().getId())
                    .stream()
                    .filter(v -> !v.getId().equals(id) && v.getDisponible())
                    .limit(3)
                    .toList();

            model.addAttribute("vehiculo", vehiculo);
            model.addAttribute("similares", similares);

            return "web/vehiculo-detalle";
        }

        return "redirect:/catalogo";
    }

    // Búsqueda avanzada
    @GetMapping("buscar")
    public String buscar(@RequestParam(required = false) Long marcaId,
                         @RequestParam(required = false) Long modeloId,
                         @RequestParam(required = false) Integer yearMin,
                         @RequestParam(required = false) Integer yearMax,
                         @RequestParam(required = false) BigDecimal precioMin,
                         @RequestParam(required = false) BigDecimal precioMax,
                         Model model) {

        List<Vehiculo> vehiculos = vehiculoService.buscarConFiltros(marcaId, modeloId, yearMin, yearMax, precioMin, precioMax);
        List<Marca> marcas = marcaService.findAllActivas();
        List<Modelo> modelos = modeloService.findAllActivos();

        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("marcas", marcas);
        model.addAttribute("modelos", modelos);

        // Mantiene los filtros seleccionados
        model.addAttribute("marcaID", marcaId);
        model.addAttribute("modeloID", modeloId);
        model.addAttribute("yearMin", yearMin);
        model.addAttribute("yearMax", yearMax);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);

        return "web/buscar";
    }

    @GetMapping("/marca/{marcaId}")
    public String vehiculosPorMarca(@PathVariable Long marcaId, Model model) {
        List<Vehiculo> vehiculos = vehiculoService.findByMarca(marcaId);
        Optional<Marca> marca = marcaService.findById(marcaId);

        model.addAttribute("vehiculos", vehiculos);
        marca.ifPresent(m -> model.addAttribute("marca", m));

        return "web/vehiculo-marca";
    }

    // API endpoint para obtener modelos por marca (AJAX)
    @GetMapping("/api/modelos/{marcaId}")
    @ResponseBody
    public List<Modelo> obtenerModelosPorMarca(@PathVariable Long marcaId) {
        return modeloService.findByMarca(marcaId);
    }
}