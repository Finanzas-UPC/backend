package com.upc.finanzas.bond.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBondCommand(
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
