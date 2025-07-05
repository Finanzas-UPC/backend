package com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BondRepository extends JpaRepository<Bond, Long> {
    List<Bond> findAllByUser_Id(Long userId);
}
