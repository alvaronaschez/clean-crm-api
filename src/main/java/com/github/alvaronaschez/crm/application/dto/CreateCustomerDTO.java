package com.github.alvaronaschez.crm.application.dto;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.github.alvaronaschez.crm.domain.Customer;
import com.github.alvaronaschez.crm.domain.User;

import lombok.Value;
import lombok.NonNull;

@Value
public class CreateCustomerDTO {
    // @formatter:off
    @NonNull String email;
    @NonNull String firstName;
    @NonNull String lastName;
    // @formatter:on

    public Customer toDomain(User modifier) {
        return new Customer(
                UUID.randomUUID(),
                email,
                firstName,
                lastName,
                Optional.empty(),
                modifier,
                Instant.now(),
                true);
    }
}
