package com.hotwheels.dealer.service;

import com.hotwheels.dealer.entity.ItemCarrito;
import com.hotwheels.dealer.entity.CarritoCompra;
import com.hotwheels.dealer.entity.Vehiculo;
import com.hotwheels.dealer.repository.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ItemCarritoService {

    @Autowired
    private ItemCarritoRepository itemRepository;

    @Autowired
    private VehiculoService vehiculoService;

    public List<ItemCarrito> findByCarrito(CarritoCompra carrito) {
        return itemRepository.findByCarritoCompra(carrito);
    }

    public ItemCarrito save(ItemCarrito item) {
        return itemRepository.save(item);
    }

    @Transactional
    public ItemCarrito agregarItem(CarritoCompra carrito, Long vehiculoId) {
        Optional<Vehiculo> vehiculoOpt = vehiculoService.findById(vehiculoId);
        if (vehiculoOpt.isPresent()) {
            Vehiculo vehiculo = vehiculoOpt.get();

            // Verificar que el vehículo esté disponible
            if (!vehiculoService.estaDisponible(vehiculoId)) {
                throw new RuntimeException("El vehículo no está disponible");
            }

            // Verificar si ya existe en el carrito
            if (existeItem(carrito, vehiculoId)) {
                throw new RuntimeException("El vehículo ya está en el carrito");
            }

            ItemCarrito item = new ItemCarrito(carrito, vehiculo);
            return save(item);
        }
        throw new RuntimeException("Vehículo no encontrado con ID: " + vehiculoId);
    }

    @Transactional
    public void removerItem(CarritoCompra carrito, Long vehiculoId) {
        Optional<Vehiculo> vehiculoOpt = vehiculoService.findById(vehiculoId);
        vehiculoOpt.ifPresent(vehiculo -> itemRepository.deleteByCarritoCompraAndVehiculo(carrito, vehiculo));
    }

    @Transactional
    public void limpiarCarrito(CarritoCompra carrito) {
        itemRepository.deleteByCarritoCompra(carrito);
    }

    public boolean existeItem(CarritoCompra carrito, Long vehiculoId) {
        Optional<Vehiculo> vehiculoOpt = vehiculoService.findById(vehiculoId);
        return vehiculoOpt.filter(vehiculo -> itemRepository.existsByCarritoCompraAndVehiculo(carrito, vehiculo)).isPresent();
    }

    public void eliminar(Long id) {
        itemRepository.deleteById(id);
    }
}