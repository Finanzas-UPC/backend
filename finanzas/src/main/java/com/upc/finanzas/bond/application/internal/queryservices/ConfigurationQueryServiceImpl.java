package com.upc.finanzas.bond.application.internal.queryservices;

import com.upc.finanzas.bond.domain.model.aggregates.Configuration;
import com.upc.finanzas.bond.domain.model.queries.GetConfigurationByIdQuery;
import com.upc.finanzas.bond.domain.model.queries.GetConfigurationByUserIdQuery;
import com.upc.finanzas.bond.domain.services.ConfigurationQueryService;
import com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories.ConfigurationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigurationQueryServiceImpl implements ConfigurationQueryService {
    private final ConfigurationRepository configurationRepository;

    public ConfigurationQueryServiceImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public Optional<Configuration> handle(GetConfigurationByUserIdQuery query) {
        return configurationRepository.findByUser_Id(query.userId());
    }

    @Override
    public Optional<Configuration> handle(GetConfigurationByIdQuery query) {
        return configurationRepository.findById(query.configurationId());
    }
}
