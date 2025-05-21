package com.hotwheels.dealer.repository;

import com.hotwheels.dealer.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    List<Marca> findByActivoTrue();

    @Query("SELECT m FROM Marca m WHERE m.activo = true ORDER BY m.nombre")
    List<Marca> findAllActivasOrdenadas();
}