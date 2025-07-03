package com.upc.finanzas.bond.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBondResource(
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
        String emissionDate,
        String gracePeriodType,
        int gracePeriodDuration,
        String currency,
        BigDecimal primeRate,
        BigDecimal structuringRate,
        BigDecimal placementRate,
        BigDecimal floatRate,
        BigDecimal cavaliRate
) {}
