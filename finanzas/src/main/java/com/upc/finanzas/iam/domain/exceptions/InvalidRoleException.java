package com.upc.finanzas.iam.domain.exceptions;

import com.upc.finanzas.iam.domain.model.valueobjects.Roles;
import java.util.List;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String role) {
        super("Rol invalido: " + role + ". Utiliza un rol de la lista: " + List.of(Roles.values()));
    }
}
