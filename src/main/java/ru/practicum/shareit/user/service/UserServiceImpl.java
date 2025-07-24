package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> emailToUser = new HashMap<>();
    private long idCounter = 1;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        return user;
    }

    @Override
    public User create(User user) {
        if (emailToUser.containsKey(user.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        user.setId(idCounter++);
        users.put(user.getId(), user);
        emailToUser.put(user.getEmail(), user);
        log.info("Created user: {}", user);
        return user;
    }

    @Override
    public User update(Long userId, User userUpdateData) {
        User existingUser = getById(userId);

        if (userUpdateData.getName() != null) {
            existingUser.setName(userUpdateData.getName());
        }

        if (userUpdateData.getEmail() != null && !userUpdateData.getEmail().equals(existingUser.getEmail())) {
            if (emailToUser.containsKey(userUpdateData.getEmail())) {
                throw new UserAlreadyExistsException("Email " + userUpdateData.getEmail() + " already in use");
            }
            emailToUser.remove(existingUser.getEmail());
            existingUser.setEmail(userUpdateData.getEmail());
            emailToUser.put(userUpdateData.getEmail(), existingUser);
        }

        users.put(userId, existingUser);
        log.info("Updated user: {}", existingUser);
        return existingUser;
    }

    @Override
    public void delete(Long userId) {
        User user = users.get(userId);
        if (user != null) {
            emailToUser.remove(user.getEmail());
        }
        users.remove(userId);
        log.info("Deleted user with id: {}", userId);
    }
}