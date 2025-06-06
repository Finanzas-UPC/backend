package com.upc.finanzas.bond.domain.services;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import com.upc.finanzas.bond.domain.model.entities.BondMetrics;
import com.upc.finanzas.bond.domain.model.entities.CashFlowItem;

import java.util.List;
import java.util.Optional;

public interface BondCalculatorService {
    List<CashFlowItem> generateCashFlowItems(Bond bond);
    Optional<BondMetrics> generateBondMetrics(Bond bond, List<CashFlowItem> cashFlowItems);
}
