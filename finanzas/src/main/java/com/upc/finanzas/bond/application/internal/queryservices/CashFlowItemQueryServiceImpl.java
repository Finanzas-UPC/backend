package com.upc.finanzas.bond.application.internal.queryservices;

import com.upc.finanzas.bond.domain.model.entities.CashFlowItem;
import com.upc.finanzas.bond.domain.model.queries.GetCashFlowByBondId;
import com.upc.finanzas.bond.domain.services.CashFlowItemQueryService;
import com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories.CashFlowItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CashFlowItemQueryServiceImpl implements CashFlowItemQueryService {
    private final CashFlowItemRepository cashFlowItemRepository;
    public CashFlowItemQueryServiceImpl(CashFlowItemRepository cashFlowItemRepository) {
        this.cashFlowItemRepository = cashFlowItemRepository;
    }

    @Override
    public List<CashFlowItem> handle(GetCashFlowByBondId query) {
        return cashFlowItemRepository.findAllByBond_Id(query.bondId());
    }
}
