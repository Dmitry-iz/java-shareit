package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User fromCreateDto(CreateUserRequestDto dto);

    @Mapping(target = "id", ignore = true)
    User fromUpdateDto(UpdateUserRequestDto dto);
}