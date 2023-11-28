package com.github.alvaronaschez.crm.application.dto;

import java.util.Optional;
import java.util.Set;

import com.github.alvaronaschez.crm.domain.UserRole;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserPartialDTO {
    @NonNull
    private final Optional<String> username;
    @NonNull
    private final Optional<String> password;
    @NonNull
    private final Optional<Set<UserRole>> roles;
}
