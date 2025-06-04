package com.upc.finanzas.iam.interfaces.exceptions;

import com.upc.finanzas.iam.domain.exceptions.InvalidPasswordException;
import com.upc.finanzas.iam.domain.exceptions.InvalidRoleException;
import com.upc.finanzas.iam.domain.exceptions.UserNotFoundInSignInException;
import com.upc.finanzas.iam.domain.exceptions.UsernameAlreadyExistsException;
import com.upc.finanzas.shared.infrastructure.interfaces.responses.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class IamExceptionsHandler {
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Nombre de usuario existente", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponseDTO> handleRoleNotFoundException(InvalidRoleException e) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Rol no encontrado", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidPasswordException(InvalidPasswordException e) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Contraseña invalida", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundInSignInException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundInSignInException(UserNotFoundInSignInException e) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Usuario no encontrado", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
