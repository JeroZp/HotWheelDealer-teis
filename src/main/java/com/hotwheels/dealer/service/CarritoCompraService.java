package com.hotwheels.dealer.service;

import com.hotwheels.dealer.entity.CarritoCompra;
import com.hotwheels.dealer.entity.Cliente;
import com.hotwheels.dealer.repository.CarritoCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CarritoCompraService {

    @Autowired
    private CarritoCompraRepository carritoRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ItemCarritoService itemCarritoService;

    public Optional<CarritoCompra> findById(Long id) {
        return carritoRepository.findById(id);
    }

    public Optional<CarritoCompra> findByCliente(Cliente cliente) {
        return carritoRepository.findByCliente(cliente);
    }

    public Optional<CarritoCompra> findByClienteId(Long clienteId) {
        return carritoRepository.findByClienteIdWithItems(clienteId);
    }

    public CarritoCompra save(CarritoCompra carrito) {
        carrito.setFechaActualizacion(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    public CarritoCompra obtenerOCrearCarrito(Long clienteId) {
        Optional<Cliente> clienteOpt = clienteService.findById(clienteId);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            // Primero, buscar por cliente_id para asegurarnos de que no hay duplicados
            Optional<CarritoCompra> carritoOpt = findByClienteId(clienteId);

            if (carritoOpt.isPresent()) {
                System.out.println("Carrito existente encontrado para cliente ID: " + clienteId);
                return carritoOpt.get();
            } else {
                // Si realmente no existe, crear uno nuevo
                System.out.println("Creando nuevo carrito para cliente ID: " + clienteId);
                CarritoCompra nuevoCarrito = new CarritoCompra(cliente);
                cliente.setCarritoCompra(nuevoCarrito);
                return save(nuevoCarrito);
            }
        }
        throw new RuntimeException("Cliente no encontrado con ID: " + clienteId);
    }

    @Transactional
    public void agregarVehiculo(Long clienteId, Long vehiculoId) {
        System.out.println("Intentando agregar vehículo " + vehiculoId + " para cliente " + clienteId);

        // Verificar si ya existe el carrito con este vehículo
        CarritoCompra carrito = obtenerOCrearCarrito(clienteId);
        if (itemCarritoService.existeItem(carrito, vehiculoId)) {
            System.out.println("El vehículo ya existe en el carrito");
            return;
        }

        itemCarritoService.agregarItem(carrito, vehiculoId);
    }

    @Transactional
    public CarritoCompra removerVehiculo(Long clienteId, Long vehiculoId) {
        Optional<CarritoCompra> carritoOpt = findByClienteId(clienteId);
        if (carritoOpt.isPresent()) {
            CarritoCompra carrito = carritoOpt.get();
            itemCarritoService.removerItem(carrito, vehiculoId);
            return carrito;
        }
        throw new RuntimeException("Carrito no encontrado para cliente ID: " + clienteId);
    }

    @Transactional
    public void limpiarCarrito(Long clienteId) {
        Optional<CarritoCompra> carritoOpt = findByClienteId(clienteId);
        if (carritoOpt.isPresent()) {
            CarritoCompra carrito = carritoOpt.get();
            itemCarritoService.limpiarCarrito(carrito);
        }
    }

    public boolean tieneVehiculo(Long clienteId, Long vehiculoId) {
        Optional<CarritoCompra> carritoOpt = findByClienteId(clienteId);
        return carritoOpt.filter(carritoCompra -> itemCarritoService.existeItem(carritoCompra, vehiculoId)).isPresent();
    }
}