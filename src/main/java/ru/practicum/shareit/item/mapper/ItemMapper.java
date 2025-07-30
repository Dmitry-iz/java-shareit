package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item fromCreateDto(CreateItemRequestDto dto, @Context User owner);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "available", source = "available")
    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "owner", ignore = true)
    void updateItemFromDto(UpdateItemRequestDto dto, @MappingTarget Item item);

    @AfterMapping
    default void setOwner(@MappingTarget Item item, @Context User owner) {
        if (owner != null) {
            item.setOwner(owner);
        }
    }
}