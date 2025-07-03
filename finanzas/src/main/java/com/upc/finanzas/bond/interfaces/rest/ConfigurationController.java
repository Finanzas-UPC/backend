package com.upc.finanzas.bond.interfaces.rest;

import com.upc.finanzas.bond.domain.exceptions.ConfigurationNotFoundException;
import com.upc.finanzas.bond.domain.model.commands.DeleteConfigurationCommand;
import com.upc.finanzas.bond.domain.model.queries.GetConfigurationByIdQuery;
import com.upc.finanzas.bond.domain.model.queries.GetConfigurationByUserIdQuery;
import com.upc.finanzas.bond.domain.services.ConfigurationCommandService;
import com.upc.finanzas.bond.domain.services.ConfigurationQueryService;
import com.upc.finanzas.bond.interfaces.rest.resources.ConfigurationResource;
import com.upc.finanzas.bond.interfaces.rest.resources.CreateConfigurationResource;
import com.upc.finanzas.bond.interfaces.rest.resources.UpdateConfigurationResource;
import com.upc.finanzas.bond.interfaces.rest.transform.ConfigurationResourceFromEntityAssembler;
import com.upc.finanzas.bond.interfaces.rest.transform.CreateConfigurationCommandFromResourceAssembler;
import com.upc.finanzas.bond.interfaces.rest.transform.UpdateConfigurationCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@RestController
@RequestMapping(value="api/v1/configuration", produces = APPLICATION_JSON_VALUE)
@Tag(name="Configuration", description = "Configuration Management Endpoints")
public class ConfigurationController {
    private final ConfigurationCommandService configurationCommandService;
    private final ConfigurationQueryService configurationQueryService;

    public ConfigurationController(ConfigurationCommandService configurationCommandService,
                                   ConfigurationQueryService configurationQueryService) {
        this.configurationCommandService = configurationCommandService;
        this.configurationQueryService = configurationQueryService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getConfigurationByUserId(@PathVariable Long userId) {
        var query = new GetConfigurationByUserIdQuery(userId);
        var configuration = configurationQueryService.handle(query);
        if (configuration.isEmpty()) return new ResponseEntity<>("Configuración no encontrada", HttpStatus.NOT_FOUND);
        var configurationResource = ConfigurationResourceFromEntityAssembler.toResourceFromEntity(configuration.get());
        return ResponseEntity.ok(configurationResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfigurationResource> updateConfiguration(@PathVariable Long id, @RequestBody UpdateConfigurationResource resource) {
        var command = UpdateConfigurationCommandFromResourceAssembler.toCommandFromResource(id, resource);
        Long configurationId = configurationCommandService.handle(command);
        var configuration = configurationQueryService.handle(new GetConfigurationByIdQuery(configurationId));
        if (configuration.isEmpty()) throw new ConfigurationNotFoundException(id);
        var configurationResource = ConfigurationResourceFromEntityAssembler.toResourceFromEntity(configuration.get());
        return ResponseEntity.ok(configurationResource);
    }

}
