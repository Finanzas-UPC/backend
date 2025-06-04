package com.upc.finanzas.iam.interfaces.rest.transform;

import com.upc.finanzas.iam.domain.model.aggregates.User;
import com.upc.finanzas.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(user.getId(), user.getUsername(), token);
    }
}