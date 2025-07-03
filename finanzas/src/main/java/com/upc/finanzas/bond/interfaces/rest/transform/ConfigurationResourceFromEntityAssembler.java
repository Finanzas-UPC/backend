package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.aggregates.Configuration;
import com.upc.finanzas.bond.interfaces.rest.resources.ConfigurationResource;

public class ConfigurationResourceFromEntityAssembler {
    public static ConfigurationResource toResourceFromEntity(Configuration entity) {
        return new ConfigurationResource(
                entity.getId(),
                entity.getUser().getId(),
                entity.getInterestType().toString(),
                entity.getCapitalization(),
                entity.getCurrency().toString()
        );
    }
}
