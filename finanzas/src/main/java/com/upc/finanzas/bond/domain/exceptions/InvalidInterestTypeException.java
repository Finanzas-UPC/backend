package com.upc.finanzas.bond.domain.exceptions;

public class InvalidInterestTypeException extends RuntimeException {
    public InvalidInterestTypeException(String interestType) {
        super("El tipo de interés '" + interestType + "' no es válido. Debe ser 'NOMINAL' o 'EFECTIVA'.");
    }
}
