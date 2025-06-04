package com.upc.finanzas.iam.application.internal.commandservices;

import com.upc.finanzas.iam.application.internal.outboundservices.hashing.HashingService;
import com.upc.finanzas.iam.application.internal.outboundservices.tokens.TokenService;
import com.upc.finanzas.iam.domain.exceptions.InvalidPasswordException;
import com.upc.finanzas.iam.domain.exceptions.InvalidRoleException;
import com.upc.finanzas.iam.domain.exceptions.UserNotFoundInSignInException;
import com.upc.finanzas.iam.domain.exceptions.UsernameAlreadyExistsException;
import com.upc.finanzas.iam.domain.model.aggregates.User;
import com.upc.finanzas.iam.domain.model.commands.SignInCommand;
import com.upc.finanzas.iam.domain.model.commands.SignUpCommand;
import com.upc.finanzas.iam.domain.services.UserCommandService;
import com.upc.finanzas.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.upc.finanzas.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByUsername(command.username());
        if (user.isEmpty()) throw new UserNotFoundInSignInException(command.username());
        if (!hashingService.matches(command.password(), user.get().getPassword())) throw new InvalidPasswordException();
        var token = tokenService.generateToken(user.get().getUsername());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    @Override
    @Transactional
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByUsername(command.username())) throw new UsernameAlreadyExistsException();
        var roles = command.roles().stream().map(role -> roleRepository.findByName(role.getName()).orElseThrow(() -> new InvalidRoleException(role.getStringName()))).toList();
        var user = new User(command.username(), hashingService.encode(command.password()), roles);
        userRepository.save(user);
        return userRepository.findByUsername(command.username());
    }
}