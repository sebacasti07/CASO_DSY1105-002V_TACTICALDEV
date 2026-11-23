package com.tacticaldev.backend.repository;

import com.tacticaldev.backend.model.InventarioRepuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioRepuesto, String> {
}
