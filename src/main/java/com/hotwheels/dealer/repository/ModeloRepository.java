package com.hotwheels.dealer.repository;

import com.hotwheels.dealer.entity.Modelo;
import com.hotwheels.dealer.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    List<Modelo> findByActivoTrue();

    List<Modelo> findByMarcaAndActivoTrue(Marca marca);

    List<Modelo> findByMarca_IdAndActivoTrue(Long marcaId);
}