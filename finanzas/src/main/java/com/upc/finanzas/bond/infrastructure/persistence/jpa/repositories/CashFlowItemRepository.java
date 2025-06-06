package com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories;

import com.upc.finanzas.bond.domain.model.entities.CashFlowItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashFlowItemRepository extends JpaRepository<CashFlowItem, Long> {
    List<CashFlowItem> findAllByBond_Id(Long bondId);
}
