package com.hotwheels.dealer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items_carrito")
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private CarritoCompra carritoCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio; // Precio del vehículo cuando se agregó al carrito

    @Column(name = "fecha_agregado", nullable = false)
    private LocalDateTime fechaAgregado = LocalDateTime.now();

    // Constructores
    public ItemCarrito() {}

    public ItemCarrito(CarritoCompra carritoCompra, Vehiculo vehiculo) {
        this.carritoCompra = carritoCompra;
        this.vehiculo = vehiculo;
        this.precio = vehiculo.getPrecio(); // Capturar precio actual
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CarritoCompra getCarritoCompra() { return carritoCompra; }
    public void setCarritoCompra(CarritoCompra carritoCompra) { this.carritoCompra = carritoCompra; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public LocalDateTime getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(LocalDateTime fechaAgregado) { this.fechaAgregado = fechaAgregado; }

    // Método utilitario
    public BigDecimal getSubtotal() {
        return precio; // En este caso es 1 vehículo por item
    }
}
