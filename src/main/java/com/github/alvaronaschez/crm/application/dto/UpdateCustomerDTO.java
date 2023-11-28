package com.github.alvaronaschez.crm.application.dto;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@RequiredArgsConstructor
@Getter
public class UpdateCustomerDTO {
    @NonNull
    private final String email;
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;
}
