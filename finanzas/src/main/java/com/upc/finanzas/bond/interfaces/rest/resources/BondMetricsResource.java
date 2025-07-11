package com.upc.finanzas.bond.interfaces.rest.resources;

import java.math.BigDecimal;

public record BondMetricsResource(
        Long id,
        Long bondId,
        BigDecimal maxPrice,
        BigDecimal duration,
        BigDecimal convexity,
        BigDecimal modifiedDuration,
        BigDecimal tcea,
        BigDecimal trea
) {}
