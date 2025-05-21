package com.hotwheels.dealer.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "clientes")
public class Cliente implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(unique = true)
    private String username; // Usar email como username

    @Column(nullable = false)
    private String password;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean activo = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "roles_cliente", joinColumns = @JoinColumn(name = "cliente_id"))
    @Column(name = "rol")
    private Set<String> roles = new HashSet<>();

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private CarritoCompra carritoCompra;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Orden> ordenes;

    // Constructores
    public Cliente() {}

    public Cliente(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.username = email; // Usar email como username automáticamente
        this.password = password;
        this.addRole("USER"); // Rol predeterminado
    }

    // Implementación de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }

    // Getters y Setters originales
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
        if (this.username == null) {
            this.username = email; // Mantener username sincronizado con email si no se ha especificado
        }
    }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public CarritoCompra getCarritoCompra() { return carritoCompra; }
    public void setCarritoCompra(CarritoCompra carritoCompra) { this.carritoCompra = carritoCompra; }

    public List<Orden> getOrdenes() { return ordenes; }
    public void setOrdenes(List<Orden> ordenes) { this.ordenes = ordenes; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public void addRole(String role) {
        this.roles.add(role);
    }
}
