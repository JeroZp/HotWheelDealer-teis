package com.hotwheels.dealer.controller.admin;

import com.hotwheels.dealer.service.VehiculoService;
import com.hotwheels.dealer.service.MarcaService;
import com.hotwheels.dealer.service.ModeloService;
import com.hotwheels.dealer.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private ModeloService modeloService;

    @Autowired
    private OrdenService ordenService;

    @GetMapping
    public String dashboard(Model model) {
        // Información básica para el dashboard
        long totalVehiculos = vehiculoService.findAll().size();
        long totalMarcas = marcaService.findAll().size();
        long totalModelos = modeloService.findAll().size();
        long totalOrdenes = ordenService.findAll().size();

        model.addAttribute("totalVehiculos", totalVehiculos);
        model.addAttribute("totalMarcas", totalMarcas);
        model.addAttribute("totalModelos", totalModelos);
        model.addAttribute("totalOrdenes", totalOrdenes);

        model.addAttribute("titulo", "Panel de Administración");

        return "admin/dashboard";
    }
}