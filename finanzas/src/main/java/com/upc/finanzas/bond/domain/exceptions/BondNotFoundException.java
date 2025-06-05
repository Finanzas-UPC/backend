package com.upc.finanzas.bond.domain.exceptions;

public class BondNotFoundException extends RuntimeException {
    public BondNotFoundException(Long bondId) {
        super("Bono con el ID " + bondId + " no fue encontrado.");
    }
}
