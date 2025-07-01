package com.upc.finanzas.bond.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBondCommand(
        Long userId,
        String name,
        BigDecimal amount,
        BigDecimal marketValue,
        int duration,
        int frequency,
        String interestType,
        BigDecimal interestRate,
        int capitalization,
        BigDecimal marketRate,
        LocalDate emissionDate,
        String gracePeriodType,
        int gracePeriodDuration,
        BigDecimal primeRate,
        BigDecimal structuringRate,
        BigDecimal placementRate,
        BigDecimal floatRate,
        BigDecimal cavaliRate
) {}
