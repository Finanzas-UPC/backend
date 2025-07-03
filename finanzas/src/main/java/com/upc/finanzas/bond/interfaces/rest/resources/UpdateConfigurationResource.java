package com.upc.finanzas.bond.interfaces.rest.resources;

public record UpdateConfigurationResource(
        String interestType,
        int capitalization,
        String currency
) {
}
