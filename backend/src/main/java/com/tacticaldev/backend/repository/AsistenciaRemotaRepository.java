package com.tacticaldev.backend.repository;

import com.tacticaldev.backend.model.AsistenciaRemota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsistenciaRemotaRepository extends JpaRepository<AsistenciaRemota, String> {
}
