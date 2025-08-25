package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void fromDto_shouldMapCorrectly() {
        ItemRequestDto dto = new ItemRequestDto("Need a drill");
        User requester = new User(1L, "User", "user@email.com");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = mapper.fromDto(dto, requester, created);

        assertThat(request.getDescription()).isEqualTo("Need a drill");
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getCreated()).isEqualTo(created);
        assertThat(request.getId()).isNull();
        assertThat(request.getItems()).isNull();
    }

    @Test
    void fromDto_shouldIgnoreIdAndItems() {
        ItemRequestDto dto = new ItemRequestDto("Need a drill");
        User requester = new User(1L, "User", "user@email.com");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = mapper.fromDto(dto, requester, created);

        assertThat(request.getId()).isNull();
        assertThat(request.getItems()).isNull();
    }

    @Test
    void toDtoWithItems_shouldMapCorrectly() {
        User requester = new User(1L, "User", "user@email.com");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = new ItemRequest(1L, "Need a drill", requester, created, Collections.emptyList());

        ItemRequestWithItemsDto dto = mapper.toDtoWithItems(request);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isEqualTo(created);
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toDtoWithItems_whenNullRequest_shouldReturnNull() {
        ItemRequestWithItemsDto dto = mapper.toDtoWithItems(null);
        assertThat(dto).isNull();
    }

    @Test
    void toDtoWithItems_whenEmptyItems_shouldMapCorrectly() {
        User requester = new User(1L, "User", "user@email.com");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = new ItemRequest(1L, "Need a drill", requester, created, Collections.emptyList());

        ItemRequestWithItemsDto dto = mapper.toDtoWithItems(request);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isEqualTo(created);
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toSimpleDto_shouldMapCorrectly() {
        ItemRequest request = new ItemRequest(1L, "Need a drill", null, LocalDateTime.now(), List.of());

        ItemRequestDto dto = mapper.toSimpleDto(request);

        assertThat(dto.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void toSimpleDto_whenNull_shouldReturnNull() {
        assertThat(mapper.toSimpleDto(null)).isNull();
    }

    @Test
    void toSimpleDto_shouldNotIncludeIdAndOtherFields() {
        User requester = new User(1L, "User", "user@email.com");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request = new ItemRequest(1L, "Need a drill", requester, created, List.of());

        ItemRequestDto dto = mapper.toSimpleDto(request);

        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto).hasAllNullFieldsOrPropertiesExcept("description");
    }

    @Test
    void toDtoList_shouldMapListCorrectly() {
        User requester = new User(1L, "User", "user@email.com");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request1 = new ItemRequest(1L, "Need a drill", requester, created, List.of());
        ItemRequest request2 = new ItemRequest(2L, "Need a hammer", requester, created.plusHours(1), List.of());

        List<ItemRequest> requests = List.of(request1, request2);

        List<ItemRequestWithItemsDto> dtos = mapper.toDtoList(requests);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getId()).isEqualTo(1L);
        assertThat(dtos.get(0).getDescription()).isEqualTo("Need a drill");
        assertThat(dtos.get(1).getId()).isEqualTo(2L);
        assertThat(dtos.get(1).getDescription()).isEqualTo("Need a hammer");
    }

    @Test
    void toDtoList_whenEmptyList_shouldReturnEmptyList() {
        List<ItemRequestWithItemsDto> dtos = mapper.toDtoList(Collections.emptyList());
        assertThat(dtos).isEmpty();
    }

    @Test
    void toDtoList_whenNullList_shouldReturnNull() {
        List<ItemRequestWithItemsDto> dtos = mapper.toDtoList(null);
        assertThat(dtos).isNull();
    }

    @Test
    void fromDto_whenNullRequester_shouldMapWithNullRequester() {
        ItemRequestDto dto = new ItemRequestDto("Need a drill");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = mapper.fromDto(dto, null, created);

        assertThat(request.getDescription()).isEqualTo("Need a drill");
        assertThat(request.getRequester()).isNull();
        assertThat(request.getCreated()).isEqualTo(created);
    }

    @Test
    void fromDto_whenNullCreated_shouldMapWithNullCreated() {
        ItemRequestDto dto = new ItemRequestDto("Need a drill");
        User requester = new User(1L, "User", "user@email.com");

        ItemRequest request = mapper.fromDto(dto, requester, null);

        assertThat(request.getDescription()).isEqualTo("Need a drill");
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getCreated()).isNull();
    }
}