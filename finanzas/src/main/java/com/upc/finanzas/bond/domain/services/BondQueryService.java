package com.upc.finanzas.bond.domain.services;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import com.upc.finanzas.bond.domain.model.queries.GetAllBondsByUserIdQuery;
import com.upc.finanzas.bond.domain.model.queries.GetBondByIdQuery;

import java.util.List;
import java.util.Optional;

public interface BondQueryService {
    List<Bond> handle(GetAllBondsByUserIdQuery query);
    Optional<Bond> handle(GetBondByIdQuery query);
}
