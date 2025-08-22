package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerAdditionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() throws Exception {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService).getAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        UserDto userDto = new UserDto(1L, "Test User", "test@email.com");
        when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@email.com"));

        verify(userService).getById(1L);
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        CreateUserRequestDto createDto = new CreateUserRequestDto();
        createDto.setName("New User");
        createDto.setEmail("new@email.com");

        UserDto createdUser = new UserDto(1L, "New User", "new@email.com");
        when(userService.create(any(CreateUserRequestDto.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("new@email.com"));

        verify(userService).create(any(CreateUserRequestDto.class));
    }

    @Test
    void updateUser_WithPartialData_ShouldReturnUpdatedUser() throws Exception {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setName("Updated Name"); // Only name, no email

        UserDto updatedUser = new UserDto(1L, "Updated Name", "original@email.com");
        when(userService.update(eq(1L), any(UpdateUserRequestDto.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("original@email.com"));

        verify(userService).update(eq(1L), any(UpdateUserRequestDto.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(userService.getById(999L)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(userService).getById(999L);
    }

    @Test
    void createUser_WhenEmailAlreadyExists_ShouldReturnConflict() throws Exception {
        CreateUserRequestDto createDto = new CreateUserRequestDto();
        createDto.setName("Test User");
        createDto.setEmail("existing@email.com");

        when(userService.create(any(CreateUserRequestDto.class)))
                .thenThrow(new UserAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already exists"));

        verify(userService).create(any(CreateUserRequestDto.class));
    }
}
