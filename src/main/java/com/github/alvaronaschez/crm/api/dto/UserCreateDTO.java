package com.github.alvaronaschez.crm.api.dto;

import java.util.Set;

import com.github.alvaronaschez.crm.domain.User;
import com.github.alvaronaschez.crm.domain.UserRole;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserCreateDTO {
    @NonNull
    private final String username;
    @NonNull
    private final String password;
    @NonNull
    private final Set<UserRole> roles;

    public @NonNull User toDomain() {
        return new User(username, password, roles);
    }
}
