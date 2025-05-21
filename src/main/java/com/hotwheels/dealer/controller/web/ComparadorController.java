package com.hotwheels.dealer.controller.web;

import com.hotwheels.dealer.entity.Vehiculo;
import com.hotwheels.dealer.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/comparar")
public class ComparadorController {

    @Autowired
    private VehiculoService vehiculoService;

    // Funcionalidad especial #2: Comparador de vehículos
    @GetMapping
    public String mostrarComparador(@RequestParam(required = false) List<Long> vehiculos, Model model) {
        List<Vehiculo> vehiculosComparar = new ArrayList<>();

        if (vehiculos != null && !vehiculos.isEmpty()) {
            for (Long vehiculoId : vehiculos) {
                Optional<Vehiculo> vehiculoOpt = vehiculoService.findById(vehiculoId);
                vehiculoOpt.ifPresent(vehiculosComparar::add);
            }
        }

        // Obtener todos los vehículos disponibles para selección
        List<Vehiculo> todosVehiculos = vehiculoService.findAllDisponibles();

        // Preparar los vehículos agrupados por marca para el select
        Map<String, List<Vehiculo>> vehiculosPorMarca = todosVehiculos.stream()
                .collect(Collectors.groupingBy(v -> v.getModelo().getMarca().getNombre()));

        model.addAttribute("vehiculosSeleccionados", vehiculosComparar);
        model.addAttribute("todosVehiculos", todosVehiculos);
        model.addAttribute("vehiculosPorMarca", vehiculosPorMarca);
        model.addAttribute("maxVehiculos", 3); // Máximo 3 vehículos para comparar

        return "web/comparador";
    }

    @PostMapping("/agregar")
    public String agregarVehiculoComparador(@RequestParam Long vehiculoId,
                                            @RequestParam(required = false) List<Long> vehiculosActuales) {
        List<Long> vehiculos = new ArrayList<>(vehiculosActuales != null ? vehiculosActuales : List.of());

        // Agregar solo si no está ya en la lista y no excede el máximo
        if (!vehiculos.contains(vehiculoId) && vehiculos.size() < 3) {
            vehiculos.add(vehiculoId);
        }

        return getString(vehiculos);
    }

    @PostMapping("/remover")
    public String removerVehiculoComparador(@RequestParam Long vehiculoId,
                                            @RequestParam(required = false) List<Long> vehiculosActuales) {
        List<Long> vehiculos = new ArrayList<>(vehiculosActuales != null ? vehiculosActuales : List.of());
        vehiculos.remove(vehiculoId);

        if (vehiculos.isEmpty()) {
            return "redirect:/comparar";
        }

        return getString(vehiculos);
    }

    private String getString(List<Long> vehiculos) {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < vehiculos.size(); i++) {
            if (i > 0) params.append("&");
            params.append("vehiculos=").append(vehiculos.get(i));
        }

        return "redirect:/comparar?" + params;
    }
}