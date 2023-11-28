package com.github.alvaronaschez.crm.application.dto;

import java.util.Set;

import com.github.alvaronaschez.crm.domain.User;
import com.github.alvaronaschez.crm.domain.UserRole;

import lombok.NonNull;
import lombok.Value;

@Value
public class CreateUserDTO {
    // @formatter:off
    @NonNull String username;
    @NonNull String password;
    @NonNull Set<UserRole> roles;
    // @formatter:on

    public @NonNull User toDomain() {
        return new User(username, password, roles);
    }
}
