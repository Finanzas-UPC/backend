package com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories;

import com.upc.finanzas.bond.domain.model.entities.BondMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BondMetricsRepository extends JpaRepository<BondMetrics, Long> {
    Optional<BondMetrics> findByBond_Id(Long bondId);
}
