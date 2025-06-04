package com.upc.finanzas.iam.domain.exceptions;

public class UserNotFoundInSignInException extends RuntimeException {
    public UserNotFoundInSignInException(String username) {
      super("No se ha encontrado el usuario con nombre: " + username);
    }
}
