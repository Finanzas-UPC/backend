package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.commands.CreateBondCommand;
import com.upc.finanzas.bond.interfaces.rest.resources.CreateBondResource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CreateBondCommandFromResourceAssembler {
    public static CreateBondCommand toCommandFromResource(CreateBondResource resource) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate parsedDate = LocalDate.parse(resource.emissionDate(), formatter);
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
