package com.github.alvaronaschez.crm.configuration.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.alvaronaschez.crm.application.UserService;
import com.github.alvaronaschez.crm.infrastructure.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        var user = userService.getActiveUserByUsername(username);
        if (user.isEmpty() || !user.get().isActive()) {
            throw new UsernameNotFoundException(
                    String.format("user with username '%s' not found", username));
        }
        return new SecurityUser(UserEntity.fromDomain(user.get()));
    }
}
