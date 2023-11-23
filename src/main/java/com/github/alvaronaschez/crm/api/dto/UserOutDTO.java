package com.github.alvaronaschez.crm.api.dto;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.alvaronaschez.crm.domain.User;
import com.github.alvaronaschez.crm.domain.UserRole;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserOutDTO {
    @NonNull
    private final UUID id;
    @NonNull
    private final String username;
    @NonNull
    private final Set<UserRole> roles;
    @JsonProperty("isActive")
    private final boolean isActive;

    public static @NonNull UserOutDTO fromDomain(@NonNull User user) {
        return new UserOutDTO(user.getId(), user.getUsername(), user.getRoles(), user.isActive());
    }
}
