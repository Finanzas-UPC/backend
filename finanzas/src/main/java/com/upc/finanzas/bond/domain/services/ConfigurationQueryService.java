package com.upc.finanzas.bond.domain.services;

import com.upc.finanzas.bond.domain.model.aggregates.Configuration;
import com.upc.finanzas.bond.domain.model.queries.GetConfigurationByIdQuery;
import com.upc.finanzas.bond.domain.model.queries.GetConfigurationByUserIdQuery;

import java.util.Optional;

public interface ConfigurationQueryService {
    Optional<Configuration> handle(GetConfigurationByUserIdQuery query);
    Optional<Configuration> handle(GetConfigurationByIdQuery query);
}
