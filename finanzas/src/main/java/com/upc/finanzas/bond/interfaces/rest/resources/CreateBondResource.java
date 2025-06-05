package com.upc.finanzas.bond.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBondResource(
        Long userId,
        String name,
        BigDecimal amount,
        int duration,
        int frequency,
        String interestType,
        BigDecimal interestRate,
        int capitalization,
        BigDecimal annualDiscountRate,
        LocalDate emissionDate,
        String gracePeriodType,
        int gracePeriodDuration
) {}
