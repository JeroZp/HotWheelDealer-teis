package com.hotwheels.dealer.service;

import com.hotwheels.dealer.entity.Cliente;
import com.hotwheels.dealer.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Métodos de búsqueda
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmailAndActivoTrue(email);
    }

    public boolean existeEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    public boolean existeById(Long id) {
        return clienteRepository.existsById(id);
    }

    // Guardar cliente
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Simple validación de credenciales
    public boolean validarCredenciales(String email, String password) {
        Optional<Cliente> clienteOpt = findByEmail(email);
        return clienteOpt.filter(cliente -> passwordEncoder.matches(password, cliente.getPassword())).isPresent();
    }
}