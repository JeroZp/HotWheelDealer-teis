package com.hotwheels.dealer.service;

import com.hotwheels.dealer.entity.Orden;
import com.hotwheels.dealer.service.payment.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProcesadorPagoService {

    private final Map<String, PagoService> serviciosPago;

    @Autowired
    public ProcesadorPagoService(List<PagoService> serviciosPago) {
        // Inyección de todas las implementaciones de PagoService
        this.serviciosPago = serviciosPago.stream()
                .collect(Collectors.toMap(
                        PagoService::obtenerNombreMetodo,
                        Function.identity()
                ));
    }

    public List<String> getMetodosPagoDisponibles() {
        return serviciosPago.keySet().stream().sorted().collect(Collectors.toList());
    }

    public boolean procesarPago(Orden orden, String metodo) {
        if (!serviciosPago.containsKey(metodo)) {
            throw new IllegalArgumentException("Método de pago no soportado: " + metodo);
        }
        return serviciosPago.get(metodo).procesarPago(orden, orden.getTotal());
    }

    public String generarComprobante(Orden orden, String metodo) {
        if (!serviciosPago.containsKey(metodo)) {
            throw new IllegalArgumentException("Método de pago no soportado: " + metodo);
        }
        return serviciosPago.get(metodo).generarComprobante(orden);
    }
}