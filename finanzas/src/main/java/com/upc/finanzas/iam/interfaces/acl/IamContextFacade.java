package com.upc.finanzas.iam.interfaces.acl;

import com.upc.finanzas.iam.domain.model.aggregates.User;
import com.upc.finanzas.iam.domain.model.commands.SignUpCommand;
import com.upc.finanzas.iam.domain.model.entities.Role;
import com.upc.finanzas.iam.domain.model.queries.GetUserByIdQuery;
import com.upc.finanzas.iam.domain.model.queries.GetUserByUsernameQuery;
import com.upc.finanzas.iam.domain.services.UserCommandService;
import com.upc.finanzas.iam.domain.services.UserQueryService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IamContextFacade {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public IamContextFacade(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    public Long createUser(String username, String password) {
        var signUpCommand = new SignUpCommand(username, password, List.of(Role.getDefaultRole()));
        var result = userCommandService.handle(signUpCommand);
        if (result.isEmpty()) return 0L;
        return result.get().getId();
    }

    public Long createUser(String username, String password, List<String> roleNames) {
        var roles = roleNames != null ? roleNames.stream().map(Role::toRoleFromName).toList() : new ArrayList<Role>();
        var signUpCommand = new SignUpCommand(username, password, roles);
        var result = userCommandService.handle(signUpCommand);
        if (result.isEmpty()) return 0L;
        return result.get().getId();
    }

    public Long fetchUserIdByUsername(String username) {
        var getUserByUsernameQuery = new GetUserByUsernameQuery(username);
        var result = userQueryService.handle(getUserByUsernameQuery);
        if (result.isEmpty()) return 0L;
        return result.get().getId();
    }

    public String fetchUsernameByUserId(Long userId) {
        var getUserByIdQuery = new GetUserByIdQuery(userId);
        var result = userQueryService.handle(getUserByIdQuery);
        if (result.isEmpty()) return Strings.EMPTY;
        return result.get().getUsername();
    }

    public Optional<User> fetchUserById(Long userId) {
        var getUserByIdQuery = new GetUserByIdQuery(userId);
        return userQueryService.handle(getUserByIdQuery);
    }

}