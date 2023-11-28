package com.github.alvaronaschez.crm.application.dto;

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
