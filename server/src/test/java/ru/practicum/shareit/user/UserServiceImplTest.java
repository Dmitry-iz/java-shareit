package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private CreateUserRequestDto createUserRequestDto;
    private UpdateUserRequestDto updateUserRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@email.com");

        userDto = new UserDto(1L, "Test User", "test@email.com");

        createUserRequestDto = new CreateUserRequestDto();
        createUserRequestDto.setName("Test User");
        createUserRequestDto.setEmail("test@email.com");

        updateUserRequestDto = new UpdateUserRequestDto();
        updateUserRequestDto.setName("Updated User");
        updateUserRequestDto.setEmail("updated@email.com");
    }

    @Test
    void getAll_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(userDto);
        verify(userRepository).findAll();
    }

    @Test
    void getById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getById(1L);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).findById(1L);
    }

    @Test
    void getById_withNonExistentId_shouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void create_shouldCreateUserSuccessfully() {
        when(userMapper.fromCreateDto(createUserRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.create(createUserRequestDto);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).save(user);
    }

    @Test
    void create_withDuplicateEmail_shouldThrowException() {
        when(userMapper.fromCreateDto(createUserRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserAlreadyExistsException.class, () -> userService.create(createUserRequestDto));
        verify(userRepository).save(user);
    }

    @Test
    void update_shouldUpdateUserSuccessfully() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@email.com");

        UserDto updatedUserDto = new UserDto(1L, "Updated User", "updated@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("updated@email.com")).thenReturn(Optional.empty());
        doNothing().when(userMapper).updateFromDto(updateUserRequestDto, user);
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(updatedUserDto);

        UserDto result = userService.update(1L, updateUserRequestDto);

        assertThat(result).isEqualTo(updatedUserDto);
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("updated@email.com");
        verify(userRepository).save(user);
    }

    @Test
    void update_withNonExistentId_shouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(999L, updateUserRequestDto));
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_withDuplicateEmail_shouldThrowException() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Other User");
        otherUser.setEmail("updated@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("updated@email.com")).thenReturn(Optional.of(otherUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.update(1L, updateUserRequestDto));
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("updated@email.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_withPartialData_shouldUpdateOnlyProvidedFields() {
        UpdateUserRequestDto partialUpdate = new UpdateUserRequestDto();
        partialUpdate.setEmail("updated@email.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Test User");
        updatedUser.setEmail("updated@email.com");

        UserDto updatedUserDto = new UserDto(1L, "Test User", "updated@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("updated@email.com")).thenReturn(Optional.empty());
        doNothing().when(userMapper).updateFromDto(partialUpdate, user);
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toUserDto(updatedUser)).thenReturn(updatedUserDto);

        UserDto result = userService.update(1L, partialUpdate);

        assertThat(result.getName()).isEqualTo("Test User"); // Name unchanged
        assertThat(result.getEmail()).isEqualTo("updated@email.com"); // Email updated
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("updated@email.com");
        verify(userRepository).save(user);
    }
}
