package com.upc.finanzas.bond.application.internal.commandservices;

import com.upc.finanzas.bond.application.internal.outboundservices.acl.ExternalUserService;
import com.upc.finanzas.bond.domain.model.aggregates.Configuration;
import com.upc.finanzas.bond.domain.model.commands.CreateConfigurationCommand;
import com.upc.finanzas.bond.domain.model.commands.DeleteConfigurationCommand;
import com.upc.finanzas.bond.domain.model.commands.UpdateConfigurationCommand;
import com.upc.finanzas.bond.domain.services.BondCommandService;
import com.upc.finanzas.bond.domain.services.BondQueryService;
import com.upc.finanzas.bond.domain.services.ConfigurationCommandService;
import com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories.ConfigurationRepository;
import com.upc.finanzas.shared.domain.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationCommandServiceImpl implements ConfigurationCommandService {
    private final ConfigurationRepository configurationRepository;
    private final BondCommandService bondCommandService;
    private final BondQueryService bondQueryService;
    private final ExternalUserService externalUserService;

    public ConfigurationCommandServiceImpl(
            ConfigurationRepository configurationRepository,
            BondCommandService bondCommandService,
            BondQueryService bondQueryService,
            ExternalUserService externalUserService) {
        this.configurationRepository = configurationRepository;
        this.bondCommandService = bondCommandService;
        this.bondQueryService = bondQueryService;
        this.externalUserService = externalUserService;
    }

    @Override
    @Transactional
    public Long handle(CreateConfigurationCommand command) {
        // Se verifica si el usuario existe
        var user = externalUserService.fetchUserById(command.userId());
        if (user.isEmpty()) throw new UserNotFoundException(command.userId());

        var configuration = new Configuration(user.get(), command);
        configurationRepository.save(configuration);

        return configuration.getId();
    }

    @Override
    @Transactional
    public Long handle(UpdateConfigurationCommand command) {
        var existingConfiguration = configurationRepository.findById(command.configurationId());
        if (existingConfiguration.isEmpty()) throw new RuntimeException("Configuration not found: " + command.configurationId());

        // Actualizar la configuración
        var configuration = existingConfiguration.get();
        configuration.update(command);
        configurationRepository.save(configuration);

        return configuration.getId();
    }

    @Override
    @Transactional
    public void handle(DeleteConfigurationCommand command) {
        var configuration = configurationRepository.findById(command.configurationId());
        if (configuration.isEmpty()) throw new RuntimeException("Configuration not found: " + command.configurationId());

        // Eliminar la configuración
        configurationRepository.delete(configuration.get());
    }
}
