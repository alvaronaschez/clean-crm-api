package com.github.alvaronaschez.crm.api.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginRequestDTO {
    @NonNull
    private final String username;
    @NonNull
    private final String password;
}
