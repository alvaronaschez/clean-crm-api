package com.github.alvaronaschez.crm.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.alvaronaschez.crm.domain.User;
import com.github.alvaronaschez.crm.domain.UserRole;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepositoryInterface userRepository;

    public void createUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findActiveUserByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        userRepository.save(user);
    }

    public Optional<User> getActiveUserByUsername(String username) {
        return userRepository.findActiveUserByUsername(username);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(UUID id) throws UserNotFoundException {
        var user = userRepository.findById(id);
        if (user.isEmpty() || !user.get().isActive()) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
    }

    public @NonNull Optional<User> partialUpdate(UUID id, Optional<String> newUsername,
            Optional<String> newPassword,
            Optional<Set<UserRole>> newRoles) {
        User currentUser;
        try {
            currentUser = userRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
        if (!currentUser.isActive()) {
            return Optional.empty(); // cannot update inactive user
        }
        if (newUsername.isPresent()
                && currentUser.getUsername() != newUsername.get()
                && userRepository.findActiveUserByUsername(newUsername.get()).isPresent()) {
            // newUsername is already taken by other active user
            return Optional.empty();
        }

        var newUser = new User(
                currentUser.getId(),
                newUsername.orElse(currentUser.getUsername()),
                newPassword.orElse(currentUser.getPassword()),
                newRoles.orElse(currentUser.getRoles()),
                currentUser.isActive());
        userRepository.save(newUser);
        return Optional.of(newUser);
    }

    public List<User> getActiveUsers() {
        return userRepository.findActive();
    }

    // public List<User> getAdminUsers() {
    // return userRepository.findAdmins();
    // }

    public static class UserAlreadyExistsException extends Exception {
    }

    public static class UserNotFoundException extends Exception {

    }
}
