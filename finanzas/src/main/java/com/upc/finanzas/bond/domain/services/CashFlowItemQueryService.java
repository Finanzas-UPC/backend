package com.upc.finanzas.bond.domain.services;

import com.upc.finanzas.bond.domain.model.entities.CashFlowItem;
import com.upc.finanzas.bond.domain.model.queries.GetCashFlowByBondId;

import java.util.List;

public interface CashFlowItemQueryService {
    List<CashFlowItem> handle(GetCashFlowByBondId query);
}
