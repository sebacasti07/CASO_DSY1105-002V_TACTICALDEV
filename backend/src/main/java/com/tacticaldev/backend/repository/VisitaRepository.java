package com.tacticaldev.backend.repository;

import com.tacticaldev.backend.model.Visita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitaRepository extends JpaRepository<Visita, String> {
}
