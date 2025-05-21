package com.hotwheels.dealer.service;

import com.hotwheels.dealer.entity.Marca;
import com.hotwheels.dealer.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaService {

    @Autowired
    private MarcaRepository marcaRepository;

    // Operaciones CRUD básicas
    public List<Marca> findAll() {
        return marcaRepository.findAll();
    }

    public List<Marca> findAllActivas() {
        return marcaRepository.findAllActivasOrdenadas();
    }

    public Optional<Marca> findById(Long id) {
        return marcaRepository.findById(id);
    }

    public Marca save(Marca marca) {
        return marcaRepository.save(marca);
    }

    public Marca crear(String nombre) {
        Marca marca = new Marca(nombre);
        return save(marca);
    }

    public Marca actualizar(Long id, String nombre) {
        Optional<Marca> marcaOpt = findById(id);
        if (marcaOpt.isPresent()) {
            Marca marca = marcaOpt.get();
            marca.setNombre(nombre);
            return save(marca);
        }
        throw new RuntimeException("Marca no encontrada con ID: " + id);
    }

    public void eliminar(Long id) {
        Optional<Marca> marcaOpt = findById(id);
        if (marcaOpt.isPresent()) {
            Marca marca = marcaOpt.get();
            marca.setActivo(false);
            save(marca);
        }
    }

    public void eliminarFisicamente(Long id) {
        marcaRepository.deleteById(id);
    }

    public boolean existeById(Long id) {
        return marcaRepository.existsById(id);
    }
}
