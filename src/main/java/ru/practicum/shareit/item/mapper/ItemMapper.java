package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item fromCreateDto(CreateItemRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item fromUpdateDto(UpdateItemRequestDto dto);
}