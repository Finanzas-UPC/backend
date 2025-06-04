package com.upc.finanzas.iam.domain.exceptions;

public class UserNotFoundInSignInException extends RuntimeException {
    public UserNotFoundInSignInException(String user) {
      super("Username not found: " + user);
    }
}
