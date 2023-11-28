package com.github.alvaronaschez.crm.application.dto;

import java.util.Optional;
import java.util.Set;

import com.github.alvaronaschez.crm.domain.UserRole;

import lombok.NonNull;
import lombok.Value;

@Value
public class UpdateUserDTO {
    // @formatter:off
    @NonNull Optional<String> username;
    @NonNull Optional<String> password;
    @NonNull Optional<Set<UserRole>> roles;
    // @formatter:on
}
