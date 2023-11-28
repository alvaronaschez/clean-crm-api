package com.github.alvaronaschez.crm.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;

@RequiredArgsConstructor
@Getter
public class Customer {
    @NonNull
    @With
    private final UUID id;

    @NonNull
    private final String email;

    @NonNull
    private final String firstName;

    @NonNull
    private final String lastName;

    @NonNull
    @With
    private final Optional<String> photo;

    @NonNull
    @With
    private final User lastModifiedBy;

    @NonNull
    private final Instant lastModifiedAt;

    private final boolean active;
}
