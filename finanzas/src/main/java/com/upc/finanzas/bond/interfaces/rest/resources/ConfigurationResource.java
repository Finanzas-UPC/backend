package com.upc.finanzas.bond.interfaces.rest.resources;

public record ConfigurationResource(
        Long id,
        Long userId,
        String interestType,
        int capitalization,
        String currency
) {
}
