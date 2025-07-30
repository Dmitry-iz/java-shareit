package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(Long userId);

    UserDto create(CreateUserRequestDto user);

    UserDto update(Long userId, UpdateUserRequestDto user);

    void delete(Long userId);

}
