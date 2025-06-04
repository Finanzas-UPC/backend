package com.upc.finanzas.iam.domain.services;

import com.upc.finanzas.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}