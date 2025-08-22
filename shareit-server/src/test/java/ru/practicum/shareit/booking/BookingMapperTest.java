package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void fromCreateDto_ShouldMapCorrectly() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Booking booking = new Booking();
        mapper.fromCreateDto(requestDto, booking, item, booker);

        assertEquals(requestDto.getStart(), booking.getStart());
        assertEquals(requestDto.getEnd(), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void updateStatus_WithApprovedTrue_ShouldSetApproved() {
        Booking booking = new Booking();
        mapper.updateStatus(true, booking);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void updateStatus_WithApprovedFalse_ShouldSetRejected() {
        Booking booking = new Booking();
        mapper.updateStatus(false, booking);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }
}