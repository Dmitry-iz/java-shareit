package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto create(CreateUserRequestDto userDto) {
        try {
            User user = userMapper.fromCreateDto(userDto);
            User savedUser = userRepository.save(user);
            log.info("Created user: {}", savedUser);
            return userMapper.toUserDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UpdateUserRequestDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            userRepository.findByEmail(userDto.getEmail())
                    .ifPresent(user -> {
                        if (!user.getId().equals(userId)) {
                            throw new UserAlreadyExistsException("Email " + userDto.getEmail() + " already in use");
                        }
                    });
        }

        userMapper.updateFromDto(userDto, existingUser);
        return userMapper.toUserDto(userRepository.save(existingUser));
    }


    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
        log.info("Deleted user with id: {}", userId);
    }
}

