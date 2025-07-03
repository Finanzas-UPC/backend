package com.upc.finanzas.bond.domain.services;

import com.upc.finanzas.bond.domain.model.commands.CreateConfigurationCommand;
import com.upc.finanzas.bond.domain.model.commands.DeleteConfigurationCommand;
import com.upc.finanzas.bond.domain.model.commands.UpdateConfigurationCommand;

public interface ConfigurationCommandService {
    Long handle(CreateConfigurationCommand command);
    Long handle(UpdateConfigurationCommand command);
    void handle(DeleteConfigurationCommand command);
}
