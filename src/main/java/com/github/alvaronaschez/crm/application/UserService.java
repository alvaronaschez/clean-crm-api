package com.github.alvaronaschez.crm.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.alvaronaschez.crm.application.dto.CreateUserDTO;
import com.github.alvaronaschez.crm.application.dto.UpdateUserDTO;
import com.github.alvaronaschez.crm.application.dto.UserOutDTO;
import com.github.alvaronaschez.crm.domain.User;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepositoryInterface userRepository;

    public @NonNull UserOutDTO createUser(CreateUserDTO user) throws UserAlreadyExistsException {
        if (userRepository.findActiveUserByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        var u = user.toDomain();
        userRepository.save(u);
        return UserOutDTO.fromDomain(u);
    }

    public @NonNull UserOutDTO getActiveUserByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findActiveUserByUsername(username).orElseThrow(UserNotFoundException::new);
        return UserOutDTO.fromDomain(user);
    }

    public @NonNull UserOutDTO getUserById(UUID id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return UserOutDTO.fromDomain(user);
    }

    public @NonNull List<UserOutDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserOutDTO.fromDomain(user))
                .collect(Collectors.toList());
    }

    public void deleteUser(UUID id) throws UserNotFoundException {
        var user = userRepository.findById(id);
        if (user.isEmpty() || !user.get().isActive()) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
    }

    public @NonNull UserOutDTO partialUpdate(UUID id, UpdateUserDTO userUpdates)
            throws UserNotFoundException, UserIllegalUpdateException, UserAlreadyExistsException {
        User currentUser;
        try {
            currentUser = userRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException();
        }
        if (!currentUser.isActive()) {
            // cannot update inactive user
            throw new UserIllegalUpdateException();
        }
        Optional<String> newUsername = userUpdates.getUsername();
        if (newUsername.isPresent()
                && currentUser.getUsername() != newUsername.get()
                && userRepository.findActiveUserByUsername(newUsername.get()).isPresent()) {
            // newUsername is already taken by other active user
            throw new UserAlreadyExistsException();
        }

        var updatedUser = new User(
                currentUser.getId(),
                userUpdates.getUsername().orElse(currentUser.getUsername()),
                userUpdates.getPassword().orElse(currentUser.getPassword()),
                userUpdates.getRoles().orElse(currentUser.getRoles()),
                currentUser.isActive());
        userRepository.save(updatedUser);
        return UserOutDTO.fromDomain(updatedUser);
    }

    public @NonNull List<UserOutDTO> getActiveUsers() {
        return userRepository.findActive().stream()
                .map(user -> UserOutDTO.fromDomain(user))
                .collect(Collectors.toList());
    }

    public static class UserAlreadyExistsException extends Exception {
    }

    public static class UserNotFoundException extends Exception {

    }

    public static class UserIllegalUpdateException extends Exception {

    }
}
