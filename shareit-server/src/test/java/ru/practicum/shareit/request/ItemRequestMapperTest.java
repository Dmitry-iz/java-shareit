package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void fromDto_shouldMapCorrectly() {
        // Given
        ItemRequestDto dto = new ItemRequestDto("Need a drill");
        User requester = new User(1L, "User", "user@email.com");
        LocalDateTime created = LocalDateTime.now();

        // When
        ItemRequest request = mapper.fromDto(dto, requester, created);

        // Then
        assertThat(request.getDescription()).isEqualTo("Need a drill");
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getCreated()).isEqualTo(created);
    }

    @Test
    void toSimpleDto_shouldMapCorrectly() {
        // Given
        ItemRequest request = new ItemRequest(1L, "Need a drill", null, LocalDateTime.now(), List.of());

        // When
        ItemRequestDto dto = mapper.toSimpleDto(request);

        // Then
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void toSimpleDto_whenNull_shouldReturnNull() {
        assertThat(mapper.toSimpleDto(null)).isNull();
    }
}