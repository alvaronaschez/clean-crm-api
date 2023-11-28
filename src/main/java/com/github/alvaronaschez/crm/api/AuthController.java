package com.github.alvaronaschez.crm.api;

import com.github.alvaronaschez.crm.application.dto.LoginRequestDTO;
import com.github.alvaronaschez.crm.application.dto.UserOutDTO;
import com.github.alvaronaschez.crm.configuration.security.SecurityUser;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/v1/me")
    @ResponseBody
    public UserOutDTO me(Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        var user = securityUser.getUser().toDomain();

        return UserOutDTO.fromDomain(user);
    }

    @PostMapping("/v1/login")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void login(@RequestBody LoginRequestDTO loginRequestDTO) {
        throw new NotImplementedException("only for Openapi to be able to autogenerate docs");
    }

    @PostMapping("/v1/logout")
    public void logout() {
        throw new NotImplementedException("only for Openapi to be able to autogenerate docs");
    }
}
