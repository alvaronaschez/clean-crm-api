package com.github.alvaronaschez.crm.infrastructure;

import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.alvaronaschez.crm.configuration.SpringProfiles;
import com.github.alvaronaschez.crm.domain.User;
import com.github.alvaronaschez.crm.domain.UserRole;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile(SpringProfiles.DEV)
public class DbInitializerDev implements ApplicationRunner {

    private final UserRepositoryImpl userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleDao roleRepository;

    @Override
    public void run(ApplicationArguments applicationArguments) {
        createTestUsers();
    }

    public void createTestUsers() {
        var role = new RoleEntity(UserRole.ADMIN);
        roleRepository.save(role);

        var admin = new User(
                "admin",
                passwordEncoder.encode("secret"),
                Set.of(UserRole.ADMIN));
        userRepository.save(admin);

        var user = new User(
                "user",
                passwordEncoder.encode("secret"),
                Set.of());
        userRepository.save(user);
    }
}