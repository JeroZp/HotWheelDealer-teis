package com.hotwheels.dealer.service.payment;

import com.hotwheels.dealer.entity.Orden;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TarjetaService implements PagoService {

    @Override
    public boolean procesarPago(Orden orden, BigDecimal monto) {
        // Simulación de procesamiento de pago con tarjeta
        System.out.println("Procesando pago con tarjeta para la orden: " + orden.getId());
        System.out.println("Monto: $" + monto);

        // Simulamos 95% de éxito en los pagos con tarjeta
        return Math.random() < 0.95;
    }

    @Override
    public String obtenerNombreMetodo() {
        return "Tarjeta de Crédito/Débito";
    }

    @Override
    public String generarComprobante(Orden orden) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "COMPROBANTE DE PAGO CON TARJETA\n" +
                "================================\n" +
                "Orden: " + orden.getId() + "\n" +
                "Fecha: " + LocalDateTime.now().format(formatter) + "\n" +
                "Cliente: " + orden.getCliente().getNombre() + "\n" +
                "Total: $" + orden.getTotal() + "\n" +
                "Método: Tarjeta de Crédito/Débito\n" +
                "Estado: PAGADO\n" +
                "Código de autorización: " + generateAuthCode();
    }

    private String generateAuthCode() {
        // Generar un código alfanumérico aleatorio de 8 caracteres
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }
}
