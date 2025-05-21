package com.hotwheels.dealer.controller.api;

import com.hotwheels.dealer.DTO.VehiculoDTO;
import com.hotwheels.dealer.entity.Vehiculo;
import com.hotwheels.dealer.exception.ResourceNotFoundException;
import com.hotwheels.dealer.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoRestController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public List<VehiculoDTO> getVehiculosEnStock() {
        // Obtener todos los vehículos en stock
        List<Vehiculo> vehiculos = vehiculoService.findAll();

        // Convertir a DTOs con enlaces directos
        return vehiculos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public VehiculoDTO getVehiculoById(@PathVariable Long id) {
        return vehiculoService.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));
    }

    private VehiculoDTO convertToDTO(Vehiculo vehiculo) {
        VehiculoDTO dto = new VehiculoDTO();
        dto.setId(vehiculo.getId());
        dto.setMarca(vehiculo.getModelo().getMarca().getNombre());
        dto.setModelo(vehiculo.getModelo().getNombre());
        dto.setYear(vehiculo.getYear());
        dto.setColor(vehiculo.getColor());
        dto.setPrecio(vehiculo.getPrecio());
        dto.setKilometraje(vehiculo.getKilometraje());
        dto.setTransmision(vehiculo.getTransmision() != null ? vehiculo.getTransmision().name() : null);
        dto.setCombustible(vehiculo.getCombustible() != null ? vehiculo.getCombustible().name() : null);

        // Añadir el enlace directo
        dto.setEnlaceDetalle("/vehiculos/" + vehiculo.getId());

        return dto;
    }
}