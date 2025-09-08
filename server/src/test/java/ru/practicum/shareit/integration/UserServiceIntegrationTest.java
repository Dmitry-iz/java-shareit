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
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void getAll_ShouldReturnAllUsers() {
        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(3);
    }

    @Test
    void getById_ShouldReturnUser() {
        UserDto result = userService.getById(user1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user1.getId());
        assertThat(result.getName()).isEqualTo("User1");
        assertThat(result.getEmail()).isEqualTo("user1@email.com");
    }

    @Test
    void getById_NonExistentUser_ShouldThrowException() {
        assertThrows(UserNotFoundException.class, () ->
                userService.getById(999L)
        );
    }

    @Test
    void create_ShouldCreateUserSuccessfully() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "New User",
                "newuser@email.com"
        );

        UserDto result = userService.create(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New User");
        assertThat(result.getEmail()).isEqualTo("newuser@email.com");
    }

    @Test
    void create_WithDuplicateEmail_ShouldThrowException() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "Duplicate",
                "user1@email.com"
        );

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.create(requestDto)
        );
    }

    @Test
    void update_ShouldUpdateUserSuccessfully() {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto(
                "updated@email.com",
                "Updated Name"
        );

        UserDto result = userService.update(user1.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void update_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto(
                null,
                "Only Name Updated"
        );

        UserDto result = userService.update(user1.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Only Name Updated");
        assertThat(result.getEmail()).isEqualTo("user1@email.com");
    }

    @Test
    void update_WithDuplicateEmail_ShouldThrowException() {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto(
                "user2@email.com",
                "Updated Name"
        );

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.update(user1.getId(), updateDto)
        );
    }

    @Test
    void delete_ShouldDeleteUserSuccessfully() {

        userService.delete(user3.getId());

        assertThrows(UserNotFoundException.class, () ->
                userService.getById(user3.getId())
        );
    }
}
