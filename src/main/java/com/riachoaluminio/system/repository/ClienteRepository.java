package com.riachoaluminio.system.repository;

import com.riachoaluminio.system.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
