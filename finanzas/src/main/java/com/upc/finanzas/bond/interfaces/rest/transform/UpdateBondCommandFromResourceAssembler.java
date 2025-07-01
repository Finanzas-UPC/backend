package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.commands.UpdateBondCommand;
import com.upc.finanzas.bond.interfaces.rest.resources.UpdateBondResource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateBondCommandFromResourceAssembler {
    public static UpdateBondCommand toCommandFromResource(Long id, UpdateBondResource resource) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate parsedDate = LocalDate.parse(resource.emissionDate(), formatter);
        return new UpdateBondCommand(
                id,
                resource.name(),
                resource.amount(),
                resource.marketValue(),
                resource.duration(),
                resource.frequency(),
                resource.interestType(),
                resource.interestRate(),
                resource.capitalization(),
                resource.marketRate(),
                parsedDate,
                resource.gracePeriodType(),
                resource.gracePeriodDuration(),
                resource.primeRate(),
                resource.structuringRate(),
                resource.placementRate(),
                resource.floatRate(),
                resource.cavaliRate()
        );
    }
}
