package com.upc.finanzas.bond.domain.exceptions;

public class ConfigurationNotFoundException extends RuntimeException {
    public ConfigurationNotFoundException(Long id) {
        super("Configuración " + id + " no fue encontrada.");
    }
}
