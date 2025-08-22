package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void getAll_ShouldReturnAllUsers() {
        // When
        List<UserDto> result = userService.getAll();

        // Then
        assertThat(result).hasSize(3);
    }

    @Test
    void getById_ShouldReturnUser() {
        // When
        UserDto result = userService.getById(user1.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user1.getId());
        assertThat(result.getName()).isEqualTo("User1");
        assertThat(result.getEmail()).isEqualTo("user1@email.com");
    }

    @Test
    void getById_NonExistentUser_ShouldThrowException() {
        // When & Then
        assertThrows(UserNotFoundException.class, () ->
                userService.getById(999L)
        );
    }

    @Test
    void create_ShouldCreateUserSuccessfully() {
        // Given
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "New User",
                "newuser@email.com"
        );

        // When
        UserDto result = userService.create(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New User");
        assertThat(result.getEmail()).isEqualTo("newuser@email.com");
    }

    @Test
    void create_WithDuplicateEmail_ShouldThrowException() {
        // Given
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "Duplicate",
                "user1@email.com" // already exists
        );

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () ->
                userService.create(requestDto)
        );
    }

    @Test
    void update_ShouldUpdateUserSuccessfully() {
        // Given
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto(
                "updated@email.com",
                "Updated Name"
        );

        // When
        UserDto result = userService.update(user1.getId(), updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void update_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Given
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto(
                null,
                "Only Name Updated"
        );

        // When
        UserDto result = userService.update(user1.getId(), updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Only Name Updated");
        assertThat(result.getEmail()).isEqualTo("user1@email.com"); // unchanged
    }

    @Test
    void update_WithDuplicateEmail_ShouldThrowException() {
        // Given
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto(
                "user2@email.com", // already used by user2
                "Updated Name"
        );

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () ->
                userService.update(user1.getId(), updateDto)
        );
    }

    @Test
    void delete_ShouldDeleteUserSuccessfully() {
        // When
        userService.delete(user3.getId());

        // Then - verify user is deleted by trying to get it
        assertThrows(UserNotFoundException.class, () ->
                userService.getById(user3.getId())
        );
    }
}
