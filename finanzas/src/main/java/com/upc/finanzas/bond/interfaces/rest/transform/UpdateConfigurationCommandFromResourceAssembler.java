package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.commands.UpdateConfigurationCommand;
import com.upc.finanzas.bond.interfaces.rest.resources.UpdateConfigurationResource;

public class UpdateConfigurationCommandFromResourceAssembler {
    public static UpdateConfigurationCommand toCommandFromResource(Long id, UpdateConfigurationResource resource) {
        return new UpdateConfigurationCommand(
                id,
                resource.interestType(),
                resource.capitalization(),
                resource.currency()
        );
    }
}
