package com.upc.finanzas.bond.interfaces.rest.transform;

import com.upc.finanzas.bond.domain.model.entities.CashFlowItem;
import com.upc.finanzas.bond.interfaces.rest.resources.CashFlowItemResource;

import java.time.format.DateTimeFormatter;

public class CashFlowItemResourceFromEntityAssembler {
    public static CashFlowItemResource toResourceFromEntity(CashFlowItem entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return new CashFlowItemResource(
                entity.getId(),
                entity.getBond().getId(),
                entity.getPeriod(),
                entity.getPaymentDate().format(formatter),
                entity.isGracePeriod(),
                entity.getInitialBalance(),
                entity.getInterest(),
                entity.getAmortization(),
                entity.getFinalBalance(),
                entity.getTotalPayment(),
                entity.getIssuerCashFlow(),
                entity.getInvestorCashFlow(),
                entity.getDiscountedFlow(),
                entity.getDiscountedFlowTimesPeriod(),
                entity.getConvexityFactor()
        );
    }
}
