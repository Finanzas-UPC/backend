package com.upc.finanzas.bond.domain.exceptions;

public class InvalidGracePeriodTypeException extends RuntimeException {
    public InvalidGracePeriodTypeException(String gracePeriodType) {
        super("El tipo de período de gracia '" + gracePeriodType + "' no es válido. Debe ser 'PARCIAL', 'TOTAL' o 'NINGUNO'.");
    }
}
