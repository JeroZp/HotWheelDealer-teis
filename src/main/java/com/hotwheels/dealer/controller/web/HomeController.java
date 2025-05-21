package com.hotwheels.dealer.controller.web;

import com.hotwheels.dealer.entity.Vehiculo;
import com.hotwheels.dealer.entity.Marca;
import com.hotwheels.dealer.service.VehiculoService;
import com.hotwheels.dealer.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private MarcaService marcaService;

    @GetMapping("/")
    public String home(Model model) {
        List<Vehiculo> ultimosVehiculos = vehiculoService.obtenerUltimosAgregados(5);

        List<Vehiculo> masVistos = vehiculoService.obtenerMasVistos(3);

        List<Marca> marcas = marcaService.findAllActivas();

        model.addAttribute("ultimosVehiculos", ultimosVehiculos);
        model.addAttribute("masVistos", masVistos);
        model.addAttribute("marcas", marcas);

        return "web/home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "web/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "web/contact";
    }
}