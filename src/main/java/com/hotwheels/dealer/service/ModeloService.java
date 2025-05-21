package com.hotwheels.dealer.service;

import com.hotwheels.dealer.entity.Modelo;
import com.hotwheels.dealer.entity.Marca;
import com.hotwheels.dealer.repository.MarcaRepository;
import com.hotwheels.dealer.repository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModeloService {

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private MarcaService marcaService;

    // Operaciones CRUD básicas
    public List<Modelo> findAll() {
        return modeloRepository.findAll();
    }

    public List<Modelo> findAllActivos() {
        return modeloRepository.findByActivoTrue();
    }

    public Optional<Modelo> findById(Long id) {
        return modeloRepository.findById(id);
    }

    public List<Modelo> findByMarca(Long marcaId) {
        return modeloRepository.findByMarca_IdAndActivoTrue(marcaId);
    }

    public Modelo save(Modelo modelo) {
        return modeloRepository.save(modelo);
    }

    public Modelo crear(String nombre, Long marcaId) {
        Optional<Marca> marcaOpt = marcaService.findById(marcaId);
        if (marcaOpt.isPresent()) {
            Modelo modelo = new Modelo(nombre, marcaOpt.get());
            return save(modelo);
        }
        throw new RuntimeException("Marca no encontrada con ID: " + marcaId);
    }

    public Modelo actualizar(Long id, String nombre, Long marcaId) {
        Optional<Modelo> modeloOpt = findById(id);
        Optional<Marca> marcaOpt = marcaService.findById(marcaId);

        if (modeloOpt.isPresent() && marcaOpt.isPresent()) {
            Modelo modelo = modeloOpt.get();
            modelo.setNombre(nombre);
            modelo.setMarca(marcaOpt.get());
            return save(modelo);
        }
        throw new RuntimeException("Modelo o Marca no encontrada");
    }

    public void eliminar(Long id) {
        Optional<Modelo> modeloOpt = findById(id);
        if (modeloOpt.isPresent()) {
            Modelo modelo = modeloOpt.get();
            modelo.setActivo(false);
            save(modelo);
        }
    }

    public void eliminarFisicamente(Long id) {
        modeloRepository.deleteById(id);
    }

    public boolean existeById(Long id) {
        return modeloRepository.existsById(id);
    }
}
