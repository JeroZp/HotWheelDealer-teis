package com.hotwheels.dealer.repository;

import com.hotwheels.dealer.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByUsername(String username);

    Optional<Cliente> findByEmailAndActivoTrue(String email);

    Optional<Cliente> findByUsernameAndActivoTrue(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<Cliente> findByActivoTrue();
}