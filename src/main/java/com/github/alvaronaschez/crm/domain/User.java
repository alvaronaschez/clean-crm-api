package com.github.alvaronaschez.crm.domain;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class User {
    @NonNull
    private final UUID id;
    @NonNull
    private final String username;
    @NonNull
    private final String password;
    @NonNull
    private final Set<UserRole> roles;
    private final boolean isActive;

    public User(String username, String password, Set<UserRole> roles) {
        this(UUID.randomUUID(), username, password, roles, true);
    }
}
