package com.upc.finanzas.bond.domain.services;

import com.upc.finanzas.bond.domain.model.entities.BondMetrics;
import com.upc.finanzas.bond.domain.model.queries.GetBondMetricsByBondId;

import java.util.Optional;

public interface BondMetricsQueryService {
    Optional<BondMetrics> handle(GetBondMetricsByBondId query);
}
