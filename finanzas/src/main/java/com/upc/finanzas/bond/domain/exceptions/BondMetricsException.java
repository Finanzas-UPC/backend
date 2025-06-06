package com.upc.finanzas.bond.domain.exceptions;

public class BondMetricsException extends RuntimeException {
    public BondMetricsException() {
        super("Hubo un error la generar las métricas del bono.");
    }
}
