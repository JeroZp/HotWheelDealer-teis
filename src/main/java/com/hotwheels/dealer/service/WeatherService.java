package com.hotwheels.dealer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public WeatherService(@Value("${openweathermap.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
    }

    public Map getClimaActual(String ciudad) {
        try {
            // Construir la URL correctamente para manejar caracteres especiales
            String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("q", ciudad)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .queryParam("lang", "es")
                    .build()
                    .encode()
                    .toUri();

            System.out.println("Llamando a la API: " + uri.toString().replace(apiKey, "API_KEY_HIDDEN"));

            // Usar Map en lugar de un DTO específico para mayor flexibilidad
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);

            // Imprimir la respuesta para depuración
            System.out.println("Respuesta recibida: " + response.getStatusCode());
            System.out.println("Cuerpo de la respuesta: " + response.getBody());

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error al obtener el clima: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}