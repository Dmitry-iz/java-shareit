package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    void fromCreateDto_WithNullParameters_ShouldHandleGracefully() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = new Booking();

        mapper.fromCreateDto(requestDto, booking, null, null);

        assertEquals(requestDto.getStart(), booking.getStart());
        assertEquals(requestDto.getEnd(), booking.getEnd());
        assertNull(booking.getItem());
        assertNull(booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void updateStatus_WithApprovedTrue_ShouldSetApproved() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);

        mapper.updateStatus(true, booking);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void updateStatus_WithApprovedFalse_ShouldSetRejected() {

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);

        mapper.updateStatus(false, booking);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void updateStatus_WithNullApproved_ShouldNotChangeStatus() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);

        mapper.updateStatus(null, booking);

        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void toDto_ShouldMapAllFieldsCorrectly() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Test Booker");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        BookingDto result = mapper.toDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
        assertNotNull(result.getBooker());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(booker.getName(), result.getBooker().getName());
        assertNotNull(result.getItem());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(item.getName(), result.getItem().getName());
    }

    @Test
    void toDto_WithNullBooker_ShouldHandleGracefully() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(null); // Null booker
        booking.setItem(item);

        BookingDto result = mapper.toDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertNull(result.getBooker());
        assertNotNull(result.getItem());
    }

    @Test
    void toDto_WithNullItem_ShouldHandleGracefully() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Test Booker");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(null);

        BookingDto result = mapper.toDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertNotNull(result.getBooker());
        assertNull(result.getItem());
    }

    @Test
    void toDto_WithNullBooking_ShouldReturnNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    void toDto_WithCompleteBooking_ShouldMapAllFields() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Test Booker");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2023, 1, 2, 10, 0));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        BookingDto result = mapper.toDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), result.getStart());
        assertEquals(LocalDateTime.of(2023, 1, 2, 10, 0), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertNotNull(result.getBooker());
        assertNotNull(result.getItem());
    }
}