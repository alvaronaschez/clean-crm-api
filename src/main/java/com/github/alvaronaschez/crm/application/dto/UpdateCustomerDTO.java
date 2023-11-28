package com.github.alvaronaschez.crm.application.dto;

import lombok.Value;
import lombok.NonNull;

@Value
public class UpdateCustomerDTO {
    // @formatter:off
    @NonNull String email;
    @NonNull String firstName;
    @NonNull
    private final String lastName;
    // @formatter:off
}
