package com.upc.finanzas.bond.domain.model.commands;

public record CreateConfigurationCommand(
        Long userId,
        String interestType,
        int capitalization,
        String currency
) {
}
