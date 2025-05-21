package com.hotwheels.dealer.repository;

import com.hotwheels.dealer.entity.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long>, JpaSpecificationExecutor<Vehiculo> {

    List<Vehiculo> findByActivoTrueAndDisponibleTrue();

    List<Vehiculo> findByModelo_Marca_Id(Long marcaId);

    List<Vehiculo> findByModelo_Id(Long modeloId);

    // Búsqueda avanzada
    @Query("SELECT v FROM Vehiculo v WHERE v.activo = true AND v.disponible = true " +
            "AND (:marcaId IS NULL OR v.modelo.marca.id = :marcaId) " +
            "AND (:modeloId IS NULL OR v.modelo.id = :modeloId) " +
            "AND (:yearMin IS NULL OR v.year >= :yearMin) " +
            "AND (:yearMax IS NULL OR v.year <= :yearMax) " +
            "AND (:precioMin IS NULL OR v.precio >= :precioMin) " +
            "AND (:precioMax IS NULL OR v.precio <= :precioMax)")
    List<Vehiculo> buscarConFiltros(@Param("marcaId") Long marcaId,
                                    @Param("modeloId") Long modeloId,
                                    @Param("yearMin") Integer yearMin,
                                    @Param("yearMax") Integer yearMax,
                                    @Param("precioMin") BigDecimal precioMin,
                                    @Param("precioMax") BigDecimal precioMax);

    // Para funcionalidad "Últimos Agregados"
    @Query("SELECT v FROM Vehiculo v WHERE v.activo = true AND v.disponible = true " +
            "ORDER BY v.fechaRegistro DESC")
    List<Vehiculo> findTopByOrderByFechaRegistroDesc();

    // Para funcionalidad "Más Vistos"
    @Query("SELECT v FROM Vehiculo v WHERE v.activo = true AND v.disponible = true " +
            "ORDER BY v.visualizaciones DESC")
    List<Vehiculo> findTopByOrderByVisualizacionesDesc();

    // Incrementar visualizaciones
    @Modifying
    @Transactional
    @Query("UPDATE Vehiculo v SET v.visualizaciones = v.visualizaciones + 1 WHERE v.id = :id")
    void incrementarVisualizaciones(@Param("id") Long id);

    Page<Vehiculo> findByActivoTrueAndDisponibleTrue(Pageable pageable);
}