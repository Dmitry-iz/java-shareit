package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ru.practicum.shareit.exception.BadRequestException;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto userDto;
    private CreateUserRequestDto createUserRequestDto;
    private UpdateUserRequestDto updateUserRequestDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Test User", "test@email.com");

        createUserRequestDto = new CreateUserRequestDto();
        createUserRequestDto.setName("Test User");
        createUserRequestDto.setEmail("test@email.com");

        updateUserRequestDto = new UpdateUserRequestDto();
        updateUserRequestDto.setName("Updated User");
        updateUserRequestDto.setEmail("updated@email.com");
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        // Arrange
        when(userService.getAll()).thenReturn(List.of(userDto));

        // Act & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[0].email").value("test@email.com"));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        // Arrange
        when(userService.getById(1L)).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void getUserById_withNonExistentId_shouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.getById(999L))
                .thenThrow(new UserNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() throws Exception {
        // Arrange
        when(userService.create(any(CreateUserRequestDto.class))).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void createUser_withInvalidData_shouldReturnBadRequest() throws Exception {
        CreateUserRequestDto invalidUser = new CreateUserRequestDto();
        invalidUser.setName(""); // Empty name
        invalidUser.setEmail("invalid-email"); // Invalid email

        // Мокируем сервис, чтобы он бросил исключение
        when(userService.create(any(CreateUserRequestDto.class)))
                .thenThrow(new BadRequestException("Validation failed"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_withDuplicateEmail_shouldReturnConflict() throws Exception {
        // Arrange
        when(userService.create(any(CreateUserRequestDto.class)))
                .thenThrow(new UserAlreadyExistsException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already exists"));
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() throws Exception {
        // Arrange
        UserDto updatedUser = new UserDto(1L, "Updated User", "updated@email.com");
        when(userService.update(anyLong(), any(UpdateUserRequestDto.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }

    @Test
    void updateUser_withNonExistentId_shouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.update(anyLong(), any(UpdateUserRequestDto.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        // Act & Assert
        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    void updateUser_withDuplicateEmail_shouldReturnConflict() throws Exception {
        // Arrange
        when(userService.update(anyLong(), any(UpdateUserRequestDto.class)))
                .thenThrow(new UserAlreadyExistsException("Email already exists"));

        // Act & Assert
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already exists"));
    }

    @Test
    void deleteUser_shouldDeleteUserSuccessfully() throws Exception {
        // Arrange
        doNothing().when(userService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_withNonExistentId_shouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new UserNotFoundException("User not found")).when(userService).delete(999L);

        // Act & Assert
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }
}