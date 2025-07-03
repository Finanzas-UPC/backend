package com.upc.finanzas.bond.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBondCommand(
        Long userId,
        String name,
        BigDecimal nominalValue,
        BigDecimal marketValue,
        int duration,
        int frequency,
        String interestType,
        BigDecimal interestRate,
        int capitalization,
        BigDecimal discountRate,
        LocalDate emissionDate,
        String gracePeriodType,
        int gracePeriodDuration,
        String currency,
        BigDecimal primeRate,
        BigDecimal structuringRate,
        BigDecimal placementRate,
        BigDecimal floatRate,
        BigDecimal cavaliRate
) {}
