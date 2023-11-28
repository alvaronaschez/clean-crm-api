package com.github.alvaronaschez.crm.application.dto;

import java.util.Set;
import java.util.UUID;

import com.github.alvaronaschez.crm.domain.User;
import com.github.alvaronaschez.crm.domain.UserRole;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class UserOutDTO {
    // @formatter:off
    @NonNull UUID id;
    @NonNull String username;
    @NonNull Set<UserRole> roles;
    private final boolean active;
    // @formatter:off

    public static @NonNull UserOutDTO fromDomain(@NonNull User user) {
        return new UserOutDTO(user.getId(), user.getUsername(), user.getRoles(), user.isActive());
    }
}
