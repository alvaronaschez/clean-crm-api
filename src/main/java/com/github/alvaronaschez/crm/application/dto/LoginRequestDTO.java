package com.github.alvaronaschez.crm.application.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class LoginRequestDTO {
    // @formatter:off
    @NonNull String username;
    @NonNull String password;
    // @formatter:on
}
