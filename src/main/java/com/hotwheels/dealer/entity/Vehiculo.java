package com.hotwheels.dealer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 50)
    private String color;

    @Column
    private Integer kilometraje;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoTransmision transmision;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoCombustible combustible;

    @Column(nullable = false)
    private Integer visualizaciones = 0;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Boolean disponible = true;

    // Enums
    public enum TipoTransmision {
        MANUAL, AUTOMATICA
    }

    public enum TipoCombustible {
        GASOLINA, DIESEL, HIBRIDO, ELECTRICO
    }

    // Constructores
    public Vehiculo() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Modelo getModelo() { return modelo; }
    public void setModelo(Modelo modelo) { this.modelo = modelo; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }

    public TipoTransmision getTransmision() { return transmision; }
    public void setTransmision(TipoTransmision transmision) { this.transmision = transmision; }

    public TipoCombustible getCombustible() { return combustible; }
    public void setCombustible(TipoCombustible combustible) { this.combustible = combustible; }

    public Integer getVisualizaciones() { return visualizaciones; }
    public void setVisualizaciones(Integer visualizaciones) { this.visualizaciones = visualizaciones; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    // Método utilitario
    public String getNombreCompleto() {
        return modelo.getMarca().getNombre() + " " + modelo.getNombre() + " " + year;
    }
}
