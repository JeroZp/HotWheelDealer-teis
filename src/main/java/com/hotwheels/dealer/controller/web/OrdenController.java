package com.hotwheels.dealer.controller.web;

import com.hotwheels.dealer.entity.Orden;
import com.hotwheels.dealer.service.OrdenService;
import com.hotwheels.dealer.service.CarritoCompraService;
import com.hotwheels.dealer.service.ProcesadorPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private CarritoCompraService carritoService;

    @Autowired
    private ProcesadorPagoService procesadorPagoService;

    @GetMapping
    public String misOrdenes(HttpSession session, Model model) {
        Long clienteId = obtenerClienteIdDeSesion(session);

        if (clienteId != null) {
            List<Orden> ordenes = ordenService.findByCliente(clienteId);
            model.addAttribute("ordenes", ordenes);
        }

        return "web/mis-ordenes";
    }

    @GetMapping("/{id}")
    public String detalleOrden(@PathVariable Long id, HttpSession session, Model model) {
        Long clienteId = obtenerClienteIdDeSesion(session);

        if (clienteId != null) {
            ordenService.findById(id).ifPresent(orden -> {
                // Verifica que la orden pertenece al cliente logueado
                if (orden.getCliente().getId().equals(clienteId)) {
                    model.addAttribute("orden", orden);
                }
            });
        }

        return "web/detalle-orden";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Long clienteId = obtenerClienteIdDeSesion(session);

        if (clienteId == null) {
            return "redirect:/login";
        }

        // Establecer carritoVacio como true por defecto
        model.addAttribute("metodosPago", procesadorPagoService.getMetodosPagoDisponibles());
        model.addAttribute("carritoVacio", true);

        carritoService.findByClienteId(clienteId).ifPresent(carrito -> {
            if (!carrito.getItems().isEmpty()) {
                model.addAttribute("carrito", carrito);
                model.addAttribute("metodosPago", procesadorPagoService.getMetodosPagoDisponibles());
                // Importante: establece carritoVacio como false cuando hay items
                model.addAttribute("carritoVacio", false);
            }
            // Si el carrito está vacío, carritoVacio ya es true por defecto
        });

        return "web/checkout";
    }

    @PostMapping("/procesar")
    public String procesarOrden(@RequestParam String metodoPago,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            Long clienteId = obtenerClienteIdDeSesion(session);

            if (clienteId == null) {
                return "redirect:/login";
            }

            // Crear la orden desde el carrito
            Orden orden = ordenService.crearOrdenDesdeCarrito(clienteId);

            // Procesar el pago
            boolean pagoExitoso = procesadorPagoService.procesarPago(orden, metodoPago);

            if (pagoExitoso) {
                // Generar comprobante y actualizar estado
                String comprobante = procesadorPagoService.generarComprobante(orden, metodoPago);
                orden.setComprobantePago(comprobante);
                ordenService.marcarComoPagada(orden.getId());

                redirectAttributes.addFlashAttribute("success",
                        "¡Orden creada y pagada exitosamente! Número de orden: " + orden.getId());
                return "redirect:/ordenes/" + orden.getId();
            } else {
                // Si el pago falla
                redirectAttributes.addFlashAttribute("error",
                        "El pago no pudo ser procesado. Por favor, intente con otro método de pago.");
                return "redirect:/ordenes/checkout";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        }
    }

    private Long obtenerClienteIdDeSesion(HttpSession session) {
        // Temporal - hasta implementar Spring Security
        return (Long) session.getAttribute("clienteId");
    }
}