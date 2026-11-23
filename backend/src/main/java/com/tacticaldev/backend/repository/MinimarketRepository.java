package com.tacticaldev.backend.repository;

import com.tacticaldev.backend.model.Minimarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinimarketRepository extends JpaRepository<Minimarket, String> {
}
