package com.upc.finanzas.iam.domain.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() { super("El nombre de usuario ya existe"); }
}
