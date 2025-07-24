package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User fromCreateDto(CreateUserRequestDto dto) {
        return new User(null, dto.getName(), dto.getEmail());
    }

    public static User fromUpdateDto(UpdateUserRequestDto dto) {
        return new User(null, dto.getName(), dto.getEmail());
    }
}
