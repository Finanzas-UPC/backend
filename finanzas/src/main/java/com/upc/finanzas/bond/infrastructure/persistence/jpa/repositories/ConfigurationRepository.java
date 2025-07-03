package com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories;

import com.upc.finanzas.bond.domain.model.aggregates.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    Optional<Configuration> findByUser_Id(Long userId);
}
