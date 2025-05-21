package com.hotwheels.dealer.service;

import com.hotwheels.dealer.entity.Orden;
import com.hotwheels.dealer.entity.Cliente;
import com.hotwheels.dealer.entity.CarritoCompra;
import com.hotwheels.dealer.entity.Vehiculo;
import com.hotwheels.dealer.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CarritoCompraService carritoService;

    @Autowired
    private VehiculoService vehiculoService;

    public List<Orden> findAll() {
        return ordenRepository.findAll();
    }

    public Optional<Orden> findById(Long id) {
        return ordenRepository.findById(id);
    }

    public List<Orden> findByCliente(Long clienteId) {
        Optional<Cliente> clienteOpt = clienteService.findById(clienteId);
        if (clienteOpt.isPresent()) {
            return ordenRepository.findByClienteOrderByFechaCreacionDesc(clienteOpt.get());
        }
        return List.of();
    }

    public List<Orden> findByEstado(Orden.EstadoOrden estado) {
        return ordenRepository.findByEstado(estado);
    }

    public Orden save(Orden orden) {
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden crearOrdenDesdeCarrito(Long clienteId) {
        // Obtener cliente
        Optional<Cliente> clienteOpt = clienteService.findById(clienteId);
        if (!clienteOpt.isPresent()) {
            throw new RuntimeException("Cliente no encontrado");
        }

        // Obtener carrito
        Optional<CarritoCompra> carritoOpt = carritoService.findByClienteId(clienteId);
        if (!carritoOpt.isPresent() || carritoOpt.get().getItems().isEmpty()) {
            throw new RuntimeException("Carrito vacío");
        }

        CarritoCompra carrito = carritoOpt.get();
        Cliente cliente = clienteOpt.get();

        // Verifica que todos los vehículos estén disponibles
        List<Vehiculo> vehiculos = carrito.getItems().stream()
                .map(item -> item.getVehiculo())
                .collect(Collectors.toList());

        for (Vehiculo vehiculo : vehiculos) {
            if (!vehiculoService.estaDisponible(vehiculo.getId())) {
                throw new RuntimeException("El vehículo " + vehiculo.getNombreCompleto() + " ya no está disponible");
            }
        }

        // Crea la orden
        Orden orden = new Orden(cliente, carrito.getTotal());
        orden.setVehiculos(vehiculos);
        orden = save(orden);

        // Marca vehículos como vendidos
        for (Vehiculo vehiculo : vehiculos) {
            vehiculoService.marcarComoVendido(vehiculo.getId());
        }

        // Limpia carrito
        carritoService.limpiarCarrito(clienteId);

        return orden;
    }

    public Orden actualizar(Long id, Orden.EstadoOrden nuevoEstado) {
        Optional<Orden> ordenOpt = findById(id);
        if (ordenOpt.isPresent()) {
            Orden orden = ordenOpt.get();
            orden.setEstado(nuevoEstado);
            orden.setFechaActualizacion(LocalDateTime.now());
            return save(orden);
        }
        throw new RuntimeException("Orden no encontrada con ID: " + id);
    }

    public void cancelarOrden(Long id) {
        actualizar(id, Orden.EstadoOrden.CANCELADA);
    }

    public void marcarComoPagada(Long id) {
        actualizar(id, Orden.EstadoOrden.PAGADA);
    }

    public void marcarComoCompletada(Long id) {
        actualizar(id, Orden.EstadoOrden.COMPLETADA);
    }

    // Métodos para estadísticas
    public Long contarOrdenesPorEstado(Orden.EstadoOrden estado) {
        return ordenRepository.countByEstado(estado);
    }

    public BigDecimal calcularTotalVentas() {
        BigDecimal total = ordenRepository.sumTotalVentasCompletadas();
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long contarOrdenesCompletadas() {
        return contarOrdenesPorEstado(Orden.EstadoOrden.COMPLETADA);
    }

    public Long contarOrdenesPendientes() {
        return contarOrdenesPorEstado(Orden.EstadoOrden.PENDIENTE);
    }
}