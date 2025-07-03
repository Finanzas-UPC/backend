package com.upc.finanzas.bond.interfaces.rest.resources;

public record CreateConfigurationResource(
        Long userId,
        String interestType,
        int capitalization,
        String currency
) {
}
