package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void fromCreateDto_WithValidDto_ShouldMapCorrectly() {
        CreateUserRequestDto dto = new CreateUserRequestDto();
        dto.setName("Test User");
        dto.setEmail("test@email.com");

        User user = mapper.fromCreateDto(dto);

        assertNotNull(user);
        assertEquals("Test User", user.getName());
        assertEquals("test@email.com", user.getEmail());
        assertNull(user.getId());
    }

    @Test
    void fromCreateDto_WithNullDto_ShouldReturnNull() {
        assertNull(mapper.fromCreateDto(null));
    }

    @Test
    void toUserDto_WithValidUser_ShouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@email.com");

        UserDto dto = mapper.toUserDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test User", dto.getName());
        assertEquals("test@email.com", dto.getEmail());
    }

    @Test
    void toUserDto_WithNullUser_ShouldReturnNull() {
        assertNull(mapper.toUserDto(null));
    }

    @Test
    void updateFromDto_WithPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Original Name");
        existingUser.setEmail("original@email.com");

        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail(null);

        mapper.updateFromDto(updateDto, existingUser);

        assertEquals("Updated Name", existingUser.getName());
        assertEquals("original@email.com", existingUser.getEmail());
        assertEquals(1L, existingUser.getId());
    }

    @Test
    void updateFromDto_WithNullDto_ShouldNotChangeUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Original Name");
        existingUser.setEmail("original@email.com");

        mapper.updateFromDto(null, existingUser);

        assertEquals("Original Name", existingUser.getName());
        assertEquals("original@email.com", existingUser.getEmail());
        assertEquals(1L, existingUser.getId());
    }

    @Test
    void updateFromDto_WithEmptyDto_ShouldNotChangeUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Original Name");
        existingUser.setEmail("original@email.com");

        UpdateUserRequestDto emptyDto = new UpdateUserRequestDto();

        mapper.updateFromDto(emptyDto, existingUser);

        assertEquals("Original Name", existingUser.getName());
        assertEquals("original@email.com", existingUser.getEmail());
        assertEquals(1L, existingUser.getId());
    }

    @Test
    void fromCreateDto_ShouldMapCorrectly() {
        CreateUserRequestDto dto = new CreateUserRequestDto("John Doe", "john@email.com");
        User user = mapper.fromCreateDto(dto);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@email.com");
        assertThat(user.getId()).isNull();
    }

    @Test
    void fromUpdateDto_ShouldMapOnlyNonNullFields() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("Jane Doe");
        dto.setEmail(null);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@email.com");

        mapper.updateFromDto(dto, existingUser);

        assertThat(existingUser.getName()).isEqualTo("Jane Doe");
        assertThat(existingUser.getEmail()).isEqualTo("old@email.com");
    }

    @Test
    void toUserDto_ShouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@email.com");

        UserDto dto = mapper.toUserDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@email.com");
    }
}
