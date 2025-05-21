package com.hotwheels.dealer.controller.web;

import com.hotwheels.dealer.entity.Marca;
import com.hotwheels.dealer.entity.Modelo;
import com.hotwheels.dealer.entity.Vehiculo;
import com.hotwheels.dealer.service.MarcaService;
import com.hotwheels.dealer.service.ModeloService;
import com.hotwheels.dealer.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private ModeloService modeloService;

    @GetMapping
    public String mostrarCatalogo(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "9") int size,
            @RequestParam(name = "sort", defaultValue = "fechaRegistro,desc") String sort,
            @RequestParam(name = "marcaId", required = false) Long marcaId,
            @RequestParam(name = "modeloId", required = false) Long modeloId,
            @RequestParam(name = "yearMin", required = false) Integer yearMin,
            @RequestParam(name = "yearMax", required = false) Integer yearMax,
            @RequestParam(name = "precioMin", required = false) BigDecimal precioMin,
            @RequestParam(name = "precioMax", required = false) BigDecimal precioMax,
            @RequestParam(name = "transmision", required = false) String transmision,
            @RequestParam(name = "combustible", required = false) String combustible,
            @RequestParam(name = "vista", defaultValue = "grid") String vista,
            Model model) {

        // Procesar el parámetro de ordenamiento
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = sortParams.length > 1 && sortParams[1].equals("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        // Crear objeto Pageable para paginación y ordenamiento
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        // Convertir transmision y combustible a enums si están presentes
        Vehiculo.TipoTransmision tipoTransmision = null;
        Vehiculo.TipoCombustible tipoCombustible = null;

        if (transmision != null && !transmision.isEmpty()) {
            try {
                tipoTransmision = Vehiculo.TipoTransmision.valueOf(transmision);
            } catch (IllegalArgumentException e) {
                // Ignorar si el valor no coincide con el enum
            }
        }

        if (combustible != null && !combustible.isEmpty()) {
            try {
                tipoCombustible = Vehiculo.TipoCombustible.valueOf(combustible);
            } catch (IllegalArgumentException e) {
                // Ignorar si el valor no coincide con el enum
            }
        }

        // Buscar vehículos con filtros y paginación
        Page<Vehiculo> paginaVehiculos = vehiculoService.findAllDisponiblesPaginado(
                pageable, marcaId, modeloId, yearMin, yearMax, precioMin, precioMax,
                tipoTransmision, tipoCombustible);

        // Obtener marcas y modelos para los filtros
        List<Marca> marcas = marcaService.findAllActivas();
        List<Modelo> modelos = modeloId != null ?
                modeloService.findByMarca(marcaId) : modeloService.findAllActivos();

        // Metadata de paginación
        model.addAttribute("vehiculos", paginaVehiculos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginaVehiculos.getTotalPages());
        model.addAttribute("totalItems", paginaVehiculos.getTotalElements());

        // Filtros aplicados
        model.addAttribute("marcaId", marcaId);
        model.addAttribute("modeloId", modeloId);
        model.addAttribute("yearMin", yearMin);
        model.addAttribute("yearMax", yearMax);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);
        model.addAttribute("transmision", transmision);
        model.addAttribute("combustible", combustible);

        // Datos para filtros
        model.addAttribute("marcas", marcas);
        model.addAttribute("modelos", modelos);
        model.addAttribute("tiposTransmision", Vehiculo.TipoTransmision.values());
        model.addAttribute("tiposCombustible", Vehiculo.TipoCombustible.values());

        // Parámetros de ordenamiento y visualización
        model.addAttribute("sort", sort);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection.toString().toLowerCase());
        model.addAttribute("reverseSortDirection", sortDirection == Sort.Direction.ASC ? "desc" : "asc");
        model.addAttribute("vista", vista);

        model.addAttribute("titulo", "Catálogo de Vehículos");

        return "web/catalogo";
    }
}