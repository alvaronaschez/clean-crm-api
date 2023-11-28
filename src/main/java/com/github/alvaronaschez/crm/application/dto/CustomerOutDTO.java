package com.github.alvaronaschez.crm.application.dto;

import java.time.Instant;
import java.util.UUID;

import com.github.alvaronaschez.crm.domain.Customer;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class CustomerOutDTO {
    // @formatter:off
    @NonNull UUID id;
    @NonNull String email;
    @NonNull String firstName;
    @NonNull String lastName;
    @NonNull Instant lastModifiedAt;
    @NonNull UUID lastModifiedBy;
    // @formatter:on

    public static CustomerOutDTO fromDomain(Customer c) {
        return new CustomerOutDTO(
                c.getId(),
                c.getEmail(),
                c.getFirstName(),
                c.getLastName(),
                c.getLastModifiedAt(),
                c.getLastModifiedBy().getId());
    }
}
