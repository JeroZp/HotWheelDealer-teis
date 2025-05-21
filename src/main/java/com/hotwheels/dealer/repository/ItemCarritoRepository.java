package com.hotwheels.dealer.repository;

import com.hotwheels.dealer.entity.ItemCarrito;
import com.hotwheels.dealer.entity.CarritoCompra;
import com.hotwheels.dealer.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByCarritoCompra(CarritoCompra carritoCompra);

    Optional<ItemCarrito> findByCarritoCompraAndVehiculo(CarritoCompra carritoCompra, Vehiculo vehiculo);

    boolean existsByCarritoCompraAndVehiculo(CarritoCompra carritoCompra, Vehiculo vehiculo);

    void deleteByCarritoCompraAndVehiculo(CarritoCompra carritoCompra, Vehiculo vehiculo);

    void deleteByCarritoCompra(CarritoCompra carritoCompra);
}