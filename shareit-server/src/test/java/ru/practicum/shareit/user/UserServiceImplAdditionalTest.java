package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplAdditionalTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAll_WhenNoUsers_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void getById_WhenUserExists_ShouldReturnUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@email.com");

        UserDto userDto = new UserDto(1L, "Test User", "test@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    void create_WhenEmailAlreadyExists_ShouldThrowUserAlreadyExistsException() {
        CreateUserRequestDto createDto = new CreateUserRequestDto();
        createDto.setName("Test User");
        createDto.setEmail("existing@email.com");

        User user = new User();
        user.setName("Test User");
        user.setEmail("existing@email.com");

        when(userMapper.fromCreateDto(createDto)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.create(createDto));

        verify(userRepository).save(user);
    }

    @Test
    void update_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setName("Updated User");
        updateDto.setEmail("updated@email.com");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.update(999L, updateDto));

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_WhenEmailExistsForDifferentUser_ShouldThrowUserAlreadyExistsException() {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setEmail("existing@email.com");

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setName("Current User");
        currentUser.setEmail("current@email.com");

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Other User");
        otherUser.setEmail("existing@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail("existing@email.com")).thenReturn(Optional.of(otherUser));

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.update(1L, updateDto));

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("existing@email.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_WithOnlyName_ShouldUpdateOnlyName() {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail(null); // Email не обновляется

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Original Name");
        existingUser.setEmail("original@email.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("original@email.com"); // Email остался прежним

        UserDto expectedDto = new UserDto(1L, "Updated Name", "original@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateFromDto(updateDto, existingUser);
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(expectedDto);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("original@email.com", result.getEmail()); // Email не изменился
        verify(userRepository).findById(1L);
        verify(userRepository, never()).findByEmail(anyString()); // findByEmail не вызывался для null email
        verify(userRepository).save(existingUser);
    }

    @Test
    void update_WithOnlyEmail_ShouldUpdateOnlyEmail() {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setName(null); // Name не обновляется
        updateDto.setEmail("updated@email.com");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Original Name");
        existingUser.setEmail("original@email.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Original Name"); // Name остался прежним
        updatedUser.setEmail("updated@email.com");

        UserDto expectedDto = new UserDto(1L, "Original Name", "updated@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("updated@email.com")).thenReturn(Optional.empty()); // Email свободен
        doNothing().when(userMapper).updateFromDto(updateDto, existingUser);
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(expectedDto);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Original Name", result.getName()); // Name не изменился
        assertEquals("updated@email.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("updated@email.com");
        verify(userRepository).save(existingUser);
    }

    @Test
    void update_WithEmptyUpdate_ShouldReturnOriginalUser() {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setName(null);
        updateDto.setEmail(null); // Пустое обновление

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Original Name");
        existingUser.setEmail("original@email.com");

        UserDto expectedDto = new UserDto(1L, "Original Name", "original@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateFromDto(updateDto, existingUser);
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toUserDto(existingUser)).thenReturn(expectedDto);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Original Name", result.getName());
        assertEquals("original@email.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository).save(existingUser);
    }
}