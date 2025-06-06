package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.commands.CreateBondCommand;
import com.upc.finanzas.bond.interfaces.rest.resources.CreateBondResource;

public class CreateBondCommandFromResourceAssembler {
    public static CreateBondCommand toCommandFromResource(CreateBondResource resource) {
        return new CreateBondCommand(
                resource.userId(),
                resource.name(),
                resource.amount(),
                resource.marketValue(),
                resource.duration(),
                resource.frequency(),
                resource.interestType(),
                resource.interestRate(),
                resource.capitalization(),
                resource.marketRate(),
                resource.emissionDate(),
                resource.gracePeriodType(),
                resource.gracePeriodDuration()
        );
    }
}
