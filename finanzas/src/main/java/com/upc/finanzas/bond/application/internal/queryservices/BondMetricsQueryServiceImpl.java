package com.upc.finanzas.bond.application.internal.queryservices;

import com.upc.finanzas.bond.domain.model.entities.BondMetrics;
import com.upc.finanzas.bond.domain.model.queries.GetBondMetricsByBondId;
import com.upc.finanzas.bond.domain.services.BondMetricsQueryService;
import com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories.BondMetricsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BondMetricsQueryServiceImpl implements BondMetricsQueryService {
    private final BondMetricsRepository bondMetricsRepository;

    public BondMetricsQueryServiceImpl(BondMetricsRepository bondMetricsRepository) {
        this.bondMetricsRepository = bondMetricsRepository;
    }

    @Override
    public Optional<BondMetrics> handle(GetBondMetricsByBondId query) {
        return bondMetricsRepository.findByBond_Id(query.bondId());
    }
}
