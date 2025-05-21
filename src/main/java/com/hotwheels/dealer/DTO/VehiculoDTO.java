package com.hotwheels.dealer.DTO;

import java.math.BigDecimal;

public class VehiculoDTO {
    private Long id;
    private String marca;
    private String modelo;
    private Integer year;
    private String color;
    private BigDecimal precio;
    private Integer kilometraje;
    private String transmision;
    private String combustible;
    private String enlaceDetalle;

    // Getters
    public Long getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public Integer getYear() { return year; }
    public String getColor() { return color; }
    public BigDecimal getPrecio() { return precio; }
    public Integer getKilometraje() { return kilometraje; }
    public String getTransmision() { return transmision; }
    public String getCombustible() { return combustible; }
    public String getEnlaceDetalle() { return enlaceDetalle; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setYear(Integer year) { this.year = year; }
    public void setColor(String color) { this.color = color; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }
    public void setTransmision(String transmision) { this.transmision = transmision; }
    public void setCombustible(String combustible) { this.combustible = combustible; }
    public void setEnlaceDetalle(String enlaceDetalle) { this.enlaceDetalle = enlaceDetalle; }
}