package com.upc.finanzas.iam.domain.model.queries;

import com.upc.finanzas.iam.domain.model.valueobjects.Roles;

public record GetRoleByNameQuery(Roles name) {
}