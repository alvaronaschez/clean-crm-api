package com.github.alvaronaschez.crm.api.dto;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.github.alvaronaschez.crm.domain.Customer;
import com.github.alvaronaschez.crm.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@RequiredArgsConstructor
@Getter
public class CustomerInDTO {
    @NonNull
    private final String email;
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;

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
