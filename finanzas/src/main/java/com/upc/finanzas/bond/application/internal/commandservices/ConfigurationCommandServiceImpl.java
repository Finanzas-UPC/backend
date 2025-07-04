package com.upc.finanzas.bond.application.internal.commandservices;

import com.upc.finanzas.bond.application.internal.outboundservices.acl.ExternalUserService;
import com.upc.finanzas.bond.domain.model.aggregates.Configuration;
import com.upc.finanzas.bond.domain.model.commands.CreateConfigurationCommand;
import com.upc.finanzas.bond.domain.model.commands.DeleteConfigurationCommand;
import com.upc.finanzas.bond.domain.model.commands.UpdateBondCommand;
import com.upc.finanzas.bond.domain.model.commands.UpdateConfigurationCommand;
import com.upc.finanzas.bond.domain.model.queries.GetAllBondsByUserIdQuery;
import com.upc.finanzas.bond.domain.services.BondCommandService;
import com.upc.finanzas.bond.domain.services.BondQueryService;
import com.upc.finanzas.bond.domain.services.ConfigurationCommandService;
import com.upc.finanzas.bond.infrastructure.persistence.jpa.repositories.ConfigurationRepository;
import com.upc.finanzas.shared.domain.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        // Considerar el tipo de cambio
        // 1 USD = 3.5 PEN = 0.88 EUR
        // 1 PEN = 0.28 USD = 0.25 EUR
        // 1 EUR = 4 PEN = 1.14 USD
        var newCurrency = command.currency();
        var newInterestType = command.interestType();
        var newCapitalization = command.capitalization();

        // Actualizar los bonos asociados
        var query = new GetAllBondsByUserIdQuery(configuration.getUser().getId());
        var bonds = bondQueryService.handle(query);
        for (var bond : bonds) {
            var newNominalValue = BigDecimal.ZERO;
            var newMarketValue = BigDecimal.ZERO;

            // Convertir los valores según el nuevo tipo de cambio
            if (newCurrency.equals(bond.getCurrency().toString())) {
                newNominalValue = bond.getNominalValue();
                newMarketValue = bond.getMarketValue();
            } else {
                switch(bond.getCurrency().toString()) {
                    case "USD":
                        if (newCurrency.equals("PEN")) {
                            newNominalValue = bond.getNominalValue().multiply(BigDecimal.valueOf(3.5));
                            newMarketValue = bond.getMarketValue().multiply(BigDecimal.valueOf(3.5));
                        } else if (newCurrency.equals("EUR")) {
                            newNominalValue = bond.getNominalValue().multiply(BigDecimal.valueOf(0.88));
                            newMarketValue = bond.getMarketValue().multiply(BigDecimal.valueOf(0.88));
                        }
                        break;
                    case "PEN":
                        if (newCurrency.equals("USD")) {
                            newNominalValue = bond.getNominalValue().multiply(BigDecimal.valueOf(0.28));
                            newMarketValue = bond.getMarketValue().multiply(BigDecimal.valueOf(0.28));
                        } else if (newCurrency.equals("EUR")) {
                            newNominalValue = bond.getNominalValue().multiply(BigDecimal.valueOf(0.25));
                            newMarketValue = bond.getMarketValue().multiply(BigDecimal.valueOf(0.25));
                        }
                        break;
                    case "EUR":
                        if (newCurrency.equals("PEN")) {
                            newNominalValue = bond.getNominalValue().multiply(BigDecimal.valueOf(4));
                            newMarketValue = bond.getMarketValue().multiply(BigDecimal.valueOf(4));
                        } else if (newCurrency.equals("USD")) {
                            newNominalValue = bond.getNominalValue().multiply(BigDecimal.valueOf(1.14));
                            newMarketValue = bond.getMarketValue().multiply(BigDecimal.valueOf(1.14));
                        }
                        break;
                }
            }

            var updateCommand = new UpdateBondCommand(
                    bond.getId(),
                    bond.getName(),
                    newNominalValue.setScale(2, RoundingMode.HALF_UP),
                    newMarketValue.setScale(2, RoundingMode.HALF_UP),
                    bond.getDuration(),
                    bond.getFrequency(),
                    newInterestType,
                    bond.getInterestRate(),
                    newCapitalization,
                    bond.getDiscountRate(),
                    bond.getEmissionDate(),
                    bond.getGracePeriodType().toString(),
                    bond.getGracePeriodDuration(),
                    newCurrency,
                    bond.getPrimeRate(),
                    bond.getStructuringRate(),
                    bond.getPlacementRate(),
                    bond.getFloatRate(),
                    bond.getCavaliRate()
            );
            bondCommandService.handle(updateCommand);
        }

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
