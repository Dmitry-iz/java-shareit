package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> emailToUser = new HashMap<>();
    private long idCounter = 1;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        return users.values().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto create(CreateUserRequestDto userDto) {
        if (emailToUser.containsKey(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }
        User user = userMapper.fromCreateDto(userDto);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        emailToUser.put(user.getEmail(), user);
        log.info("Created user: {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UpdateUserRequestDto userDto) {
        User existingUser = users.get(userId);
        if (existingUser == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (emailToUser.containsKey(userDto.getEmail())) {
                throw new UserAlreadyExistsException("Email " + userDto.getEmail() + " already in use");
            }
            emailToUser.remove(existingUser.getEmail());
            emailToUser.put(userDto.getEmail(), existingUser);
        }

        userMapper.updateUserFromDto(userDto, existingUser);

        users.put(userId, existingUser);
        log.info("Updated user: {}", existingUser);
        return userMapper.toUserDto(existingUser);
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