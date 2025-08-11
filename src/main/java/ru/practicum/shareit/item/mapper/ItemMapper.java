package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;

import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CommentMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemDto toItemDto(Item item);

    @Mapping(target = "lastBooking", expression = "java(mapBookingInfo(lastBookings))")
    @Mapping(target = "nextBooking", expression = "java(mapBookingInfo(nextBookings))")
    @Mapping(target = "comments", source = "comments")
    ItemDto toDtoWithBookings(
            Item item,
            @Param("lastBookings") List<Booking> lastBookings,
            @Param("nextBookings") List<Booking> nextBookings,
            List<CommentDto> comments
    );

    default ItemDto.BookingInfo mapBookingInfo(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) return null;
        ItemDto.BookingInfo info = new ItemDto.BookingInfo();
        info.setId(bookings.get(0).getId());
        info.setBookerId(bookings.get(0).getBooker().getId());
        return info;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "available", source = "dto.available")
    @Mapping(target = "owner", source = "owner")
    Item fromCreateDto(CreateItemRequestDto dto, User owner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(UpdateItemRequestDto dto, @MappingTarget Item item);
}
