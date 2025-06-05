package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import com.upc.finanzas.bond.interfaces.rest.resources.BondResource;

public class BondResourceFromEntityAssembler {
    public static BondResource toResourceFromEntity(Bond entity) {
        return new BondResource(
                entity.getId(),
                entity.getUser().getId(),
                entity.getName(),
                entity.getAmount(),
                entity.getDuration(),
                entity.getFrequency(),
                entity.getInterestType().toString(),
                entity.getInterestRate(),
                entity.getCapitalization(),
                entity.getAnnualDiscountRate(),
                entity.getEmissionDate(),
                entity.getGracePeriodType().toString(),
                entity.getGracePeriodDuration()
        );
    }
}
