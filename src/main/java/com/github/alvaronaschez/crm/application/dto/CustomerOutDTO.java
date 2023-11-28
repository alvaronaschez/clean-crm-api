package com.github.alvaronaschez.crm.application.dto;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.github.alvaronaschez.crm.domain.Customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CustomerOutDTO {
    @NonNull
    private final UUID id;
    @NonNull
    private final String email;
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;
    @NonNull
    private final Optional<Instant> lastModifiedAt;
    @NonNull
    private final Optional<UUID> lastModifiedBy;

    public static CustomerOutDTO fromDomain(Customer c) {
        return new CustomerOutDTO(
                c.getId(),
                c.getEmail(),
                c.getFirstName(),
                c.getLastName(),
                Optional.of(c.getLastModifiedAt()),
                Optional.of(c.getLastModifiedBy().getId()));
    }
}
