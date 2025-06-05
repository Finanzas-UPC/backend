package com.upc.finanzas.bond.application.internal.queryservices;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import com.upc.finanzas.bond.domain.model.queries.GetAllBondsByUserIdQuery;
import com.upc.finanzas.bond.domain.model.queries.GetBondByIdQuery;
import com.upc.finanzas.bond.domain.services.BondQueryService;
import com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories.BondRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BondQueryServiceImpl implements BondQueryService {
    private final BondRepository bondRepository;

    public BondQueryServiceImpl(BondRepository bondRepository) {
        this.bondRepository = bondRepository;
    }

    @Override
    public List<Bond> handle(GetAllBondsByUserIdQuery query) {
        return bondRepository.findAllByUser_Id(query.userId());
    }

    @Override
    public Optional<Bond> handle(GetBondByIdQuery query) {
        return bondRepository.findById(query.bondId());
    }
}