package com.upc.finanzas.bond.domain.model.commands;

public record UpdateConfigurationCommand(
        Long configurationId,
        String interestType,
        int capitalization,
        String currency
) {
}
