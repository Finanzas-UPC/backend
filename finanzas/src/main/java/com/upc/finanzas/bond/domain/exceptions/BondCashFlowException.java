package com.upc.finanzas.bond.domain.exceptions;

public class BondCashFlowException extends RuntimeException {
    public BondCashFlowException() {
        super("Hubo un error al generar los flujos de caja del bono.");
    }
}
