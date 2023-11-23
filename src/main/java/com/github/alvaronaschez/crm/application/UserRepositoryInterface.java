package com.github.alvaronaschez.crm.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.github.alvaronaschez.crm.domain.User;

public interface UserRepositoryInterface {
    public Optional<User> findById(UUID id);

    public Optional<User> findActiveUserByUsername(String username);

    public List<User> findAll();

    public void save(User user);

    public void deleteById(UUID id);

    public List<User> findActive();

    // public List<User> findAdmins();
}
