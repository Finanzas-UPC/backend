package com.upc.finanzas.bond.interfaces.exceptions;

import com.upc.finanzas.bond.domain.exceptions.*;
import com.upc.finanzas.shared.infrastructure.interfaces.responses.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BondExceptionHandler {
    @ExceptionHandler(BondNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBondNotFoundException(BondNotFoundException e) {
        ErrorResponseDTO error = new ErrorResponseDTO("Bono no encontrado por ID", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidGracePeriodTypeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidGracePeriodTypeException(InvalidGracePeriodTypeException e) {
        ErrorResponseDTO error = new ErrorResponseDTO("Tipo de período de gracia inválido", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInterestTypeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidInterestTypeException(InvalidInterestTypeException e) {
        ErrorResponseDTO error = new ErrorResponseDTO("Tipo de interés inválido", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BondCashFlowException.class)
    public ResponseEntity<ErrorResponseDTO> handleBondCashFlowException(BondCashFlowException e) {
        ErrorResponseDTO error = new ErrorResponseDTO("Flujos de caja no generados", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BondMetricsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBondMetricsException(BondMetricsException e) {
        ErrorResponseDTO error = new ErrorResponseDTO("Métricas del bono no generadas", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
