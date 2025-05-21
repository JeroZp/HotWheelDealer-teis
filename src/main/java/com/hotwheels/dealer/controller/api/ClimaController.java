package com.hotwheels.dealer.controller.api;

import com.hotwheels.dealer.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/clima")
public class ClimaController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getClima(@RequestParam(defaultValue = "Medellin") String ciudad) {
        try {
            Map resultado = weatherService.getClimaActual(ciudad);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener datos del clima: " + e.getMessage());
            return ResponseEntity.ok(error); // Devolvemos 200 pero con un objeto de error
        }
    }
}