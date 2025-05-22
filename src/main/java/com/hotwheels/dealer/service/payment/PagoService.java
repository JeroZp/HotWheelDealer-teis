package com.hotwheels.dealer.service.payment;

import com.hotwheels.dealer.entity.Orden;
import java.math.BigDecimal;

public interface PagoService {
    boolean procesarPago(Orden orden, BigDecimal monto);
    String obtenerNombreMetodo();
    String generarComprobante(Orden orden);
}
