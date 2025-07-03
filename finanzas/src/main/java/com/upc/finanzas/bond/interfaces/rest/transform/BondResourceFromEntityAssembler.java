package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.aggregates.Bond;
import com.upc.finanzas.bond.interfaces.rest.resources.BondResource;

import java.time.format.DateTimeFormatter;

public class BondResourceFromEntityAssembler {
    public static BondResource toResourceFromEntity(Bond entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return new BondResource(
                entity.getId(),
                entity.getUser().getId(),
                entity.getName(),
                entity.getNominalValue(),
                entity.getMarketValue(),
                entity.getDuration(),
                entity.getFrequency(),
                entity.getInterestType().toString(),
                entity.getInterestRate(),
                entity.getCapitalization(),
                entity.getDiscountRate(),
                entity.getEmissionDate().format(formatter),
                entity.getGracePeriodType().toString(),
                entity.getGracePeriodDuration(),
                entity.getCurrency().toString(),
                entity.getPrimeRate(),
                entity.getStructuringRate(),
                entity.getPlacementRate(),
                entity.getFloatRate(),
                entity.getCavaliRate()
        );
    }
}
