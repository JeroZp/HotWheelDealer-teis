package com.hotwheels.dealer.repository;

import com.hotwheels.dealer.entity.Orden;
import com.hotwheels.dealer.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByCliente(Cliente cliente);

    List<Orden> findByClienteOrderByFechaCreacionDesc(Cliente cliente);

    List<Orden> findByEstado(Orden.EstadoOrden estado);

    @Query("SELECT o FROM Orden o WHERE o.cliente.id = :clienteId ORDER BY o.fechaCreacion DESC")
    List<Orden> findByClienteIdOrderByFechaCreacionDesc(@Param("clienteId") Long clienteId);

    // Para estadísticas del admin
    @Query("SELECT COUNT(o) FROM Orden o WHERE o.estado = :estado")
    Long countByEstado(@Param("estado") Orden.EstadoOrden estado);

    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.estado = 'COMPLETADA'")
    BigDecimal sumTotalVentasCompletadas();
}