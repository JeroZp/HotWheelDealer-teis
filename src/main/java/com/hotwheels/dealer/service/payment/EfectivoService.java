package com.hotwheels.dealer.service.payment;

import com.hotwheels.dealer.entity.Orden;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EfectivoService implements PagoService {

    @Override
    public boolean procesarPago(Orden orden, BigDecimal monto) {
        // Simulación de procesamiento de pago en efectivo
        System.out.println("Registrando pago en efectivo para la orden: " + orden.getId());
        System.out.println("Monto: $" + monto);

        // En efectivo siempre es exitoso (es una simulación)
        return true;
    }

    @Override
    public String obtenerNombreMetodo() {
        return "Efectivo";
    }

    @Override
    public String generarComprobante(Orden orden) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "COMPROBANTE DE PAGO EN EFECTIVO\n" +
                "==============================\n" +
                "Orden: " + orden.getId() + "\n" +
                "Fecha: " + LocalDateTime.now().format(formatter) + "\n" +
                "Cliente: " + orden.getCliente().getNombre() + "\n" +
                "Total: $" + orden.getTotal() + "\n" +
                "Método: Efectivo\n" +
                "Estado: PAGADO\n" +
                "Código de recibo: " + generateReceiptNumber();
    }

    private String generateReceiptNumber() {
        // Generar un número de recibo único
        return "REC-" + System.currentTimeMillis() % 10000;
    }
}
