package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserMapper.class})
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "created", source = "created")
    @Mapping(target = "items", ignore = true)
    ItemRequest fromDto(
            ItemRequestDto requestDto,
            User requester,
            LocalDateTime created
    );

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "created", source = "request.created")
    @Mapping(target = "items", source = "request.items")
    ItemRequestWithItemsDto toDtoWithItems(ItemRequest request);

    default ItemRequestDto toSimpleDto(ItemRequest request) {
        if (request == null) {
            return null;
        }
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(request.getDescription());
        return dto;
    }

    List<ItemRequestWithItemsDto> toDtoList(List<ItemRequest> requests);
}