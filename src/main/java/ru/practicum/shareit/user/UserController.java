package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        User user = userService.getById(userId);
        return ResponseEntity.ok(userMapper.toUserDto(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserRequestDto userDto) {
        User user = userMapper.fromCreateDto(userDto);
        User createdUser = userService.create(user);
        return ResponseEntity.ok(userMapper.toUserDto(createdUser));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId,
                                              @RequestBody @Valid UpdateUserRequestDto userDto) {
        User user = userMapper.fromUpdateDto(userDto);
        User updatedUser = userService.update(userId, user);
        return ResponseEntity.ok(userMapper.toUserDto(updatedUser));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}