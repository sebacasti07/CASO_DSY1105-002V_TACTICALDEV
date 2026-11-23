package com.tacticaldev.backend.repository;

import com.tacticaldev.backend.model.EquipoRefrigeracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipoRepository extends JpaRepository<EquipoRefrigeracion, String> {
}
