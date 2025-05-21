package com.hotwheels.dealer.config;

import com.hotwheels.dealer.entity.*;
import com.hotwheels.dealer.repository.ClienteRepository;
import com.hotwheels.dealer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private ModeloService modeloService;

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    protected void cargarClientesAutenticacion() {
        // Crear usuario admin si no existe
        if (clienteRepository.findByUsername("admin").isEmpty()) {
            Cliente admin = new Cliente();
            admin.setNombre("Administrador");
            admin.setUsername("admin");
            admin.setEmail("admin@hotwheels.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActivo(true);
            admin.addRole("ADMIN");
            admin.addRole("USER");
            clienteRepository.save(admin);
            System.out.println("👤 Usuario administrador creado");
        }

        // Crear usuario normal si no existe
        if (clienteRepository.findByUsername("user").isEmpty()) {
            Cliente user = new Cliente();
            user.setNombre("Usuario Regular");
            user.setUsername("user");
            user.setEmail("user@hotwheels.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setActivo(true);
            user.addRole("USER");
            clienteRepository.save(user);
            System.out.println("👤 Usuario regular creado");
        }
    }

    private void cargarClientes() {
        // Verificar si ya existen clientes con estos emails
        if (clienteRepository.findByEmail("user@example.com").isEmpty()) {
            Cliente clienteUser = new Cliente();
            clienteUser.setNombre("Usuario Regular");
            clienteUser.setEmail("user@example.com");
            clienteUser.setPassword("user123");
            clienteUser.setActivo(true);
            Cliente savedUser = clienteRepository.save(clienteUser);
            System.out.println("Cliente para user creado con ID: " + savedUser.getId());

            // Actualiza el ClienteIdSessionFilter para usar este ID
        }

        if (clienteRepository.findByEmail("admin@example.com").isEmpty()) {
            Cliente clienteAdmin = new Cliente();
            clienteAdmin.setNombre("Administrador");
            clienteAdmin.setEmail("admin@example.com");
            clienteAdmin.setPassword("admin123");
            clienteAdmin.setActivo(true);
            Cliente savedAdmin = clienteRepository.save(clienteAdmin);
            System.out.println("Cliente para admin creado con ID: " + savedAdmin.getId());
        }
    }

    @Override
    @Transactional // Importante: Evita problemas de lazy loading
    public void run(String... args) {
        System.out.println("🔄 DataLoader ejecutándose...");
        cargarClientes();
        cargarClientesAutenticacion();

        long cantidadVehiculos = vehiculoService.findAll().size();
        System.out.println("📊 Vehículos existentes: " + cantidadVehiculos);

        if (cantidadVehiculos < 10) {
            System.out.println("🚀 Cargando datos de prueba...");
            cargarTodosDatos();
            System.out.println("🚗 Datos de prueba cargados exitosamente!");
        } else {
            System.out.println("ℹ️ Ya hay suficientes vehículos (" + cantidadVehiculos + ")");
        }

        System.out.println("📊 FINAL - Total vehículos: " + vehiculoService.findAll().size());
    }

    @Transactional
    protected void cargarTodosDatos() {
        // 1. Crear marcas si no existen
        List<Marca> marcas = marcaService.findAll();
        if (marcas.isEmpty()) {
            marcaService.crear("Toyota");
            marcaService.crear("Ford");
            marcaService.crear("Chevrolet");
            marcaService.crear("Honda");
            marcas = marcaService.findAll();
        }

        // 2. Crear modelos para cada marca
        for (Marca marca : marcas) {
            List<Modelo> modelosExistentes = modeloService.findByMarca(marca.getId());
            if (modelosExistentes.isEmpty()) {
                switch (marca.getNombre()) {
                    case "Toyota":
                        modeloService.crear("Corolla", marca.getId());
                        modeloService.crear("Camry", marca.getId());
                        break;
                    case "Ford":
                        modeloService.crear("Mustang", marca.getId());
                        modeloService.crear("Fusion", marca.getId());
                        break;
                    case "Chevrolet":
                        modeloService.crear("Spark", marca.getId());
                        modeloService.crear("Cruze", marca.getId());
                        break;
                    case "Honda":
                        modeloService.crear("Civic", marca.getId());
                        modeloService.crear("Accord", marca.getId());
                        break;
                }
            }
        }

        // 3. Crear vehículos
        List<Modelo> todosModelos = modeloService.findAllActivos();
        if (!todosModelos.isEmpty()) {
            crearVehiculos(todosModelos);
        }
    }

    private void crearVehiculos(List<Modelo> modelos) {
        Random random = new Random();
        String[] colores = {"Blanco", "Negro", "Rojo", "Azul", "Gris", "Plata", "Verde"};
        Vehiculo.TipoTransmision[] transmisiones = {
                Vehiculo.TipoTransmision.MANUAL,
                Vehiculo.TipoTransmision.AUTOMATICA
        };
        Vehiculo.TipoCombustible[] combustibles = {
                Vehiculo.TipoCombustible.GASOLINA,
                Vehiculo.TipoCombustible.DIESEL,
                Vehiculo.TipoCombustible.HIBRIDO
        };

        // Crear 15 vehículos
        for (int i = 0; i < 15; i++) {
            Modelo modelo = modelos.get(random.nextInt(modelos.size()));
            int year = 2015 + random.nextInt(9); // 2015-2023
            BigDecimal precio = new BigDecimal(15000 + random.nextInt(35000));
            String color = colores[random.nextInt(colores.length)];
            int kilometraje = random.nextInt(150000);
            Vehiculo.TipoTransmision transmision = transmisiones[random.nextInt(transmisiones.length)];
            Vehiculo.TipoCombustible combustible = combustibles[random.nextInt(combustibles.length)];

            vehiculoService.crear(modelo.getId(), year, precio, color,
                    kilometraje, transmision, combustible);
        }
    }
}