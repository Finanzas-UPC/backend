package com.upc.finanzas.bond.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CashFlowItemResource(
        Long id,
        Long bondId,
        int period,
        String paymentDate,
        boolean isGracePeriod,
        BigDecimal initialBalance,
        BigDecimal interest,
        BigDecimal amortization,
        BigDecimal finalBalance,
        BigDecimal totalPayment,
        BigDecimal issuerCashFlow,
        BigDecimal bondHolderCashFlow
) { }
