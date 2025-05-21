package com.hotwheels.dealer.service;

import com.hotwheels.dealer.entity.Marca;
import com.hotwheels.dealer.entity.Vehiculo;
import com.hotwheels.dealer.entity.Modelo;
import com.hotwheels.dealer.repository.VehiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ModeloService modeloService;

    // Operaciones CRUD básicas
    public List<Vehiculo> findAll() {
        return vehiculoRepository.findAll();
    }

    public List<Vehiculo> findAllDisponibles() {
        return vehiculoRepository.findByActivoTrueAndDisponibleTrue();
    }

    public Optional<Vehiculo> findById(Long id) {
        return vehiculoRepository.findById(id);
    }

    public Vehiculo save(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    public Vehiculo crear(Long modeloId, Integer year, BigDecimal precio, String color,
                          Integer kilometraje, Vehiculo.TipoTransmision transmision,
                          Vehiculo.TipoCombustible combustible) {
        Optional<Modelo> modeloOpt = modeloService.findById(modeloId);
        if (modeloOpt.isPresent()) {
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setModelo(modeloOpt.get());
            vehiculo.setYear(year);
            vehiculo.setPrecio(precio);
            vehiculo.setColor(color);
            vehiculo.setKilometraje(kilometraje);
            vehiculo.setTransmision(transmision);
            vehiculo.setCombustible(combustible);
            return save(vehiculo);
        }
        throw new RuntimeException("Modelo no encontrado con ID: " + modeloId);
    }

    public void eliminar(Long id) {
        Optional<Vehiculo> vehiculoOpt = findById(id);
        if (vehiculoOpt.isPresent()) {
            Vehiculo vehiculo = vehiculoOpt.get();
            vehiculo.setActivo(false);
            save(vehiculo);
        }
    }

    public void marcarComoVendido(Long id) {
        Optional<Vehiculo> vehiculoOpt = findById(id);
        if (vehiculoOpt.isPresent()) {
            Vehiculo vehiculo = vehiculoOpt.get();
            vehiculo.setDisponible(false);
            save(vehiculo);
        }
    }

    // Funcionalidades especiales
    public List<Vehiculo> buscarConFiltros(Long marcaId, Long modeloId, Integer yearMin,
                                           Integer yearMax, BigDecimal precioMin, BigDecimal precioMax) {
        return vehiculoRepository.buscarConFiltros(marcaId, modeloId, yearMin, yearMax, precioMin, precioMax);
    }

    public List<Vehiculo> obtenerUltimosAgregados(int cantidad) {
        return vehiculoRepository.findTopByOrderByFechaRegistroDesc()
                .stream()
                .limit(cantidad)
                .toList();
    }

    public List<Vehiculo> obtenerMasVistos(int cantidad) {
        return vehiculoRepository.findTopByOrderByVisualizacionesDesc()
                .stream()
                .limit(cantidad)
                .toList();
    }

    /**
     * Busca vehículos disponibles con paginación y filtros
     */
    @Transactional(readOnly = true)
    public Page<Vehiculo> findAllDisponiblesPaginado(
            Pageable pageable,
            Long marcaId,
            Long modeloId,
            Integer yearMin,
            Integer yearMax,
            BigDecimal precioMin,
            BigDecimal precioMax,
            Vehiculo.TipoTransmision transmision,
            Vehiculo.TipoCombustible combustible) {

        // Construye la especificación para la búsqueda dinámica
        Specification<Vehiculo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtros básicos: activo y disponible
            predicates.add(criteriaBuilder.equal(root.get("activo"), true));
            predicates.add(criteriaBuilder.equal(root.get("disponible"), true));

            // Filtro por marca
            if (marcaId != null) {
                Join<Vehiculo, Modelo> modeloJoin = root.join("modelo");
                Join<Modelo, Marca> marcaJoin = modeloJoin.join("marca");
                predicates.add(criteriaBuilder.equal(marcaJoin.get("id"), marcaId));
            }

            // Filtro por modelo
            if (modeloId != null) {
                Join<Vehiculo, Modelo> modeloJoin = root.join("modelo");
                predicates.add(criteriaBuilder.equal(modeloJoin.get("id"), modeloId));
            }

            // Filtro por rango de año
            if (yearMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("year"), yearMin));
            }
            if (yearMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("year"), yearMax));
            }

            // Filtro por rango de precio
            if (precioMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), precioMin));
            }
            if (precioMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), precioMax));
            }

            // Filtro por transmisión
            if (transmision != null) {
                predicates.add(criteriaBuilder.equal(root.get("transmision"), transmision));
            }

            // Filtro por combustible
            if (combustible != null) {
                predicates.add(criteriaBuilder.equal(root.get("combustible"), combustible));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return vehiculoRepository.findAll(spec, pageable);
    }

    @Transactional
    public void incrementarVisualizaciones(Long id) {
        vehiculoRepository.incrementarVisualizaciones(id);
    }

    public List<Vehiculo> findByMarca(Long marcaId) {
        return vehiculoRepository.findByModelo_Marca_Id(marcaId);
    }

    public List<Vehiculo> findByModelo(Long modeloId) {
        return vehiculoRepository.findByModelo_Id(modeloId);
    }

    public boolean existeById(Long id) {
        return vehiculoRepository.existsById(id);
    }

    public boolean estaDisponible(Long id) {
        Optional<Vehiculo> vehiculoOpt = findById(id);
        return vehiculoOpt.map(v -> v.getActivo() && v.getDisponible()).orElse(false);
    }
}