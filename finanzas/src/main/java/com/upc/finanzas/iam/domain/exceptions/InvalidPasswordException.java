package com.upc.finanzas.iam.domain.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("La contraseña es inválida");
    }
}
