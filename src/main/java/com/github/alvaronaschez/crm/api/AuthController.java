package com.github.alvaronaschez.crm.api;

import com.github.alvaronaschez.crm.api.dto.UserOutDTO;
import com.github.alvaronaschez.crm.configuration.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
}
