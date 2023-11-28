package com.github.alvaronaschez.crm.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.github.alvaronaschez.crm.application.UserService;
import com.github.alvaronaschez.crm.application.UserService.UserAlreadyExistsException;
import com.github.alvaronaschez.crm.application.UserService.UserIllegalUpdateException;
import com.github.alvaronaschez.crm.application.UserService.UserNotFoundException;
import com.github.alvaronaschez.crm.application.dto.CreateUserDTO;
import com.github.alvaronaschez.crm.application.dto.UserOutDTO;
import com.github.alvaronaschez.crm.application.dto.UpdateUserDTO;
import com.github.alvaronaschez.crm.configuration.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserOutDTO create(@RequestBody CreateUserDTO user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        var u = new CreateUserDTO(user.getUsername(), encodedPassword, user.getRoles());
        try {
            return userService.createUser(u);
        } catch (UserAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UserOutDTO user(@PathVariable UUID id) {

        try {
            return userService.getUserById(id);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<UserOutDTO> list() {
        return userService.getUsers();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UserOutDTO partialUpdate(@PathVariable UUID id, @RequestBody UpdateUserDTO user,
            Authentication authentication) {
        var encodedPassword = user.getPassword().map(p -> passwordEncoder.encode(p));
        user = new UpdateUserDTO(user.getUsername(), encodedPassword, user.getRoles());

        UserOutDTO response;
        try {
            response = userService.partialUpdate(id, user);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (UserAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (UserIllegalUpdateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // update session if current logged in user has changed
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        var loggedInUser = securityUser.getUser().toDomain();
        if (loggedInUser.getId() == response.getId()) {
            // TODO: update session
        }

        return response;

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/active")
    @ResponseStatus(code = HttpStatus.OK)
    public List<UserOutDTO> active() {
        return userService.getActiveUsers();
    }

    // @GetMapping("/admins")
    // @ResponseStatus(code = HttpStatus.OK)
    // public List<UserOutDTO> admins() {
    // return userService.getAdminUsers().stream()
    // .map(user -> UserOutDTO.fromDomain(user))
    // .collect(Collectors.toList());
    // }

}
