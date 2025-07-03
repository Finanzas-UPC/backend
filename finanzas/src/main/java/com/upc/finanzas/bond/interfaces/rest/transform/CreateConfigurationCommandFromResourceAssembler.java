package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.commands.CreateConfigurationCommand;
import com.upc.finanzas.bond.interfaces.rest.resources.CreateConfigurationResource;

public class CreateConfigurationCommandFromResourceAssembler {
    public static CreateConfigurationCommand toCommandFromResource(CreateConfigurationResource resource) {
        return new CreateConfigurationCommand(
                resource.userId(),
                resource.interestType(),
                resource.capitalization(),
                resource.currency()
        );
    }
}
