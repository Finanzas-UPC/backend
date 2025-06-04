package com.upc.finanzas.iam.interfaces.rest.transform;

import com.upc.finanzas.iam.domain.model.entities.Role;
import com.upc.finanzas.iam.interfaces.rest.resources.RoleResource;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());
    }
}