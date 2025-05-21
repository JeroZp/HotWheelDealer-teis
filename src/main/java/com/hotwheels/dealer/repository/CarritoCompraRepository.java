package com.hotwheels.dealer.repository;

import com.hotwheels.dealer.entity.CarritoCompra;
import com.hotwheels.dealer.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoCompraRepository extends JpaRepository<CarritoCompra, Long> {

    Optional<CarritoCompra> findByCliente(Cliente cliente);

    Optional<CarritoCompra> findByClienteId(Long clienteId);

    @Query("SELECT c FROM CarritoCompra c LEFT JOIN FETCH c.items WHERE c.cliente.id = :clienteId")
    Optional<CarritoCompra> findByClienteIdWithItems(@Param("clienteId") Long clienteId);
}