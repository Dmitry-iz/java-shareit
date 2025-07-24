package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(Long userId);

    User create(User user);

    User update(Long userId, User user);

    void delete(Long userId);
}
