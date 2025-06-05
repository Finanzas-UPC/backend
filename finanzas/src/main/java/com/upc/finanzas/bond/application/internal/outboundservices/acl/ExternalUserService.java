package com.upc.finanzas.bond.application.internal.outboundservices.acl;

import com.upc.finanzas.iam.domain.model.aggregates.User;
import com.upc.finanzas.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExternalUserService {
    private final IamContextFacade userContextFacade;

    public ExternalUserService(IamContextFacade userContextFacade) {
        this.userContextFacade = userContextFacade;
    }

    public Optional<User> fetchUserById(Long userId) {
        return userContextFacade.fetchUserById(userId);
    }
}
