package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.commands.UpdateBondCommand;
import com.upc.finanzas.bond.interfaces.rest.resources.UpdateBondResource;

public class UpdateBondCommandFromResourceAssembler {
    public static UpdateBondCommand toCommandFromResource(Long id, UpdateBondResource resource) {
        return new UpdateBondCommand(
                id,
                resource.name(),
                resource.amount(),
                resource.duration(),
                resource.frequency(),
                resource.interestType(),
                resource.interestRate(),
                resource.capitalization(),
                resource.annualDiscountRate(),
                resource.emissionDate(),
                resource.gracePeriodType(),
                resource.gracePeriodDuration()
        );
    }
}
