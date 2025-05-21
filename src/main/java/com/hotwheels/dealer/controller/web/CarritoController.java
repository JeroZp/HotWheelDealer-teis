package com.hotwheels.dealer.controller.web;

import com.hotwheels.dealer.entity.CarritoCompra;
import com.hotwheels.dealer.service.CarritoCompraService;
import com.hotwheels.dealer.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoCompraService carritoService;

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        Long clienteId = obtenerClienteIdDeSesion(session);

        if (clienteId != null) {
            Optional<CarritoCompra> carritoOpt = carritoService.findByClienteId(clienteId);
            carritoOpt.ifPresent(carrito -> model.addAttribute("carrito", carrito));
        }

        return "web/carrito";
    }

    @PostMapping("/agregar/{vehiculoId}")
    public String agregarVehiculo(@PathVariable Long vehiculoId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            Long clienteId = obtenerClienteIdDeSesion(session);

            if (clienteId == null) {
                redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para agregar vehículos al carrito");
                return "redirect:/login";
            }

            carritoService.agregarVehiculo(clienteId, vehiculoId);
            redirectAttributes.addFlashAttribute("success", "Vehículo agregado al carrito");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/vehiculos/" + vehiculoId;
    }

    @PostMapping("/remover/{vehiculoId}")
    public String removerVehiculo(@PathVariable Long vehiculoId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            Long clienteId = obtenerClienteIdDeSesion(session);

            if (clienteId == null) {
                redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión");
                return "redirect:/login";
            }

            carritoService.removerVehiculo(clienteId, vehiculoId);
            redirectAttributes.addFlashAttribute("success", "Vehículo removido del carrito");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito";
    }

    @PostMapping("/limpiar")
    public String limpiarCarrito(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Long clienteId = obtenerClienteIdDeSesion(session);

            if (clienteId == null) {
                redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión");
                return "redirect:/login";
            }

            carritoService.limpiarCarrito(clienteId);
            redirectAttributes.addFlashAttribute("success", "Carrito vaciado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito";
    }

    // API endpoints para AJAX
    @PostMapping("/api/agregar/{vehiculoId}")
    @ResponseBody
    public ResponseEntity<?> agregarVehiculoAjax(@PathVariable Long vehiculoId, HttpSession session) {
        try {
            Long clienteId = obtenerClienteIdDeSesion(session);

            if (clienteId == null) {
                return ResponseEntity.badRequest().body("Debe iniciar sesión");
            }

            carritoService.agregarVehiculo(clienteId, vehiculoId);
            return ResponseEntity.ok().body("Vehículo agregado al carrito");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/test-login")
    public String testLogin(HttpSession session, RedirectAttributes attributes) {
        session.setAttribute("clienteId", 1L); // Usa un ID de cliente que exista en tu BD
        attributes.addFlashAttribute("success", "Sesión de prueba iniciada. ClienteId: 1");
        return "redirect:/";
    }

    @GetMapping("/api/contador")
    @ResponseBody
    public int contadorCarrito(HttpSession session) {
        Long clienteId = obtenerClienteIdDeSesion(session);

        if (clienteId != null) {
            Optional<CarritoCompra> carritoOpt = carritoService.findByClienteId(clienteId);
            return carritoOpt.map(CarritoCompra::getCantidadItems).orElse(0);
        }

        return 0;
    }

    private Long obtenerClienteIdDeSesion(HttpSession session) {
        return (Long) session.getAttribute("clienteId");
    }
}