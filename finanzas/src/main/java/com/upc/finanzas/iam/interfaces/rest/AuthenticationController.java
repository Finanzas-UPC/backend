package com.upc.finanzas.iam.interfaces.rest;

import com.upc.finanzas.bond.domain.model.commands.CreateConfigurationCommand;
import com.upc.finanzas.bond.domain.services.ConfigurationCommandService;
import com.upc.finanzas.iam.domain.services.UserCommandService;
import com.upc.finanzas.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.upc.finanzas.iam.interfaces.rest.resources.SignInResource;
import com.upc.finanzas.iam.interfaces.rest.resources.SignUpResource;
import com.upc.finanzas.iam.interfaces.rest.resources.UserResource;
import com.upc.finanzas.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.upc.finanzas.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.upc.finanzas.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.upc.finanzas.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Authentication Endpoints")
public class AuthenticationController {
    private final UserCommandService userCommandService;
    private final ConfigurationCommandService configurationCommandService;

    public AuthenticationController(UserCommandService userCommandService,
                                    ConfigurationCommandService configurationCommandService) {
        this.userCommandService = userCommandService;
        this.configurationCommandService = configurationCommandService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResource> signUp(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var user = userCommandService.handle(signUpCommand);
        if (user.isEmpty()) return ResponseEntity.badRequest().build();
        // Initialize configuration for the new user
        Long configurationId = configurationCommandService.handle(new CreateConfigurationCommand(user.get().getId(), "NOMINAL", 30, "PEN"));
        if (configurationId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return new ResponseEntity<>(userResource, HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticatedUserResource> signIn(@RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedUser = userCommandService.handle(signInCommand);
        if (authenticatedUser.isEmpty()) return ResponseEntity.notFound().build();
        var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(authenticatedUser.get().getLeft(), authenticatedUser.get().getRight());
        return ResponseEntity.ok(authenticatedUserResource);
    }
}