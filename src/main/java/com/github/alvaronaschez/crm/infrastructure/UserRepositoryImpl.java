package com.github.alvaronaschez.crm.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import com.github.alvaronaschez.crm.application.UserRepositoryInterface;
import com.github.alvaronaschez.crm.domain.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Getter
public class UserRepositoryImpl implements UserRepositoryInterface {
    private final UserDao userRepository;

    @Override
    public Optional<User> findActiveUserByUsername(String username) {
        return userRepository.findByUsername(username).map(u -> u.toDomain());
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(u -> u.toDomain());
    }

    @Override
    public void save(User user) {
        userRepository.save(UserEntity.fromDomain(user));
    }

    @Override
    public List<User> findAll() {
        var users = userRepository.findAll();
        var stream = StreamSupport.stream(users.spliterator(), false);
        return stream.map(u -> u.toDomain()).collect(Collectors.toList());
    }

    // @Override
    // public List<User> findAdmins() {
    // var adminRol = new RoleEntity(UserRole.ADMIN);
    // return userRepository.findByRoles(Set.of(adminRol))
    // .stream().map(u -> u.toDomain()).collect(Collectors.toList());
    // }

    @Override
    public List<User> findActive() {
        return userRepository.findByActive(true)
                .stream().map(u -> u.toDomain()).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

}
