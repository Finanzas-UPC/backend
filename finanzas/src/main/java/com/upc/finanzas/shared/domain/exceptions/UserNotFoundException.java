package com.upc.finanzas.shared.domain.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Usuario no encontrado");
    }

    public UserNotFoundException(Long userId) {
        super("Usuario con ID " + userId + " no fue encontrado");
    }
}
