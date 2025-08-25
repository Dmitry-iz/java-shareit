package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void fromCreateDto_WithNullRequestDto_ShouldHandleGracefully() {
        User booker = new User();
        Item item = new Item();
        Booking booking = new Booking();

        mapper.fromCreateDto(null, booking, item, booker);

        assertNull(booking.getStart());
        assertNull(booking.getEnd());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void fromCreateDto_WithEmptyBookingTarget_ShouldPopulateAllFields() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(1L);
        booker.setName("Test Booker");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        Booking booking = new Booking();

        mapper.fromCreateDto(requestDto, booking, item, booker);

        assertNotNull(booking);
        assertEquals(requestDto.getStart(), booking.getStart());
        assertEquals(requestDto.getEnd(), booking.getEnd());
        assertEquals(booker, booking.getBooker());
        assertEquals(item, booking.getItem());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertNull(booking.getId());
    }

    @Test
    void fromCreateDto_WithPrepopulatedBooking_ShouldOverrideFieldsButKeepId() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Booking booking = new Booking();
        booking.setId(999L);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now());
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(new User());
        booking.setItem(new Item());

        mapper.fromCreateDto(requestDto, booking, item, booker);

        assertEquals(999L, booking.getId());
        assertEquals(requestDto.getStart(), booking.getStart());
        assertEquals(requestDto.getEnd(), booking.getEnd());
        assertEquals(booker, booking.getBooker());
        assertEquals(item, booking.getItem());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void mapperInstance_ShouldNotBeNull() {
        assertNotNull(mapper, "Mapper instance should not be null");
    }

    @Test
    void mapper_ShouldBeInstanceOfGeneratedImplementation() {
        assertTrue(mapper instanceof BookingMapperImpl,
                "Mapper should be instance of generated implementation");
    }

    @Test
    void fromCreateDto_ShouldRespectMappingAnnotations() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Booking booking = new Booking();
        booking.setId(100L);

        mapper.fromCreateDto(requestDto, booking, item, booker);

        assertEquals(100L, booking.getId());

        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void fromCreateDto_WithSameStartAndEndTime_ShouldHandleCorrectly() {
        LocalDateTime sameTime = LocalDateTime.now();

        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setStart(sameTime);
        requestDto.setEnd(sameTime);

        Booking booking = new Booking();

        mapper.fromCreateDto(requestDto, booking, null, null);

        assertEquals(sameTime, booking.getStart());
        assertEquals(sameTime, booking.getEnd());
    }

    @Test
    void updateStatus_WithDifferentInitialStatuses_ShouldOverrideStatus() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);

        mapper.updateStatus(false, booking);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void toDto_WithDifferentStatuses_ShouldMapCorrectly() {
        User booker = new User();
        Item item = new Item();

        for (BookingStatus status : BookingStatus.values()) {
            Booking booking = new Booking();
            booking.setId(1L);
            booking.setStatus(status);
            booking.setBooker(booker);
            booking.setItem(item);

            BookingDto result = mapper.toDto(booking);

            assertEquals(status, result.getStatus());
        }
    }

    @Test
    void toDto_ShouldCreateNewInstance_NotModifyOriginal() {
        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto result = mapper.toDto(booking);

        booking.setStatus(BookingStatus.APPROVED);

        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void fromCreateDto_WithNullDatesInRequestDto_ShouldHandleGracefully() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();

        Booking booking = new Booking();

        mapper.fromCreateDto(requestDto, booking, null, null);

        assertNull(booking.getStart());
        assertNull(booking.getEnd());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void fromCreateDto_ShouldPerformWellWithMultipleCalls() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setStart(LocalDateTime.now());
        requestDto.setEnd(LocalDateTime.now().plusDays(1));

        User booker = new User();
        Item item = new Item();
        Booking booking = new Booking();

        long startTime = System.nanoTime();

        for (int i = 0; i < 1000; i++) {
            mapper.fromCreateDto(requestDto, booking, item, booker);
        }

        long duration = System.nanoTime() - startTime;

        assertTrue(duration < 1_000_000_000, "Mapping should be efficient");
    }

    @Test
    void toDto_And_Back_ShouldBeConsistent() {
        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Booking original = new Booking();
        original.setId(1L);
        original.setStart(LocalDateTime.now());
        original.setEnd(LocalDateTime.now().plusDays(1));
        original.setStatus(BookingStatus.WAITING);
        original.setBooker(booker);
        original.setItem(item);

        BookingDto dto = mapper.toDto(original);

        assertEquals(original.getId(), dto.getId());
        assertEquals(original.getStart(), dto.getStart());
        assertEquals(original.getEnd(), dto.getEnd());
        assertEquals(original.getStatus(), dto.getStatus());
        assertEquals(original.getBooker().getId(), dto.getBooker().getId());
        assertEquals(original.getItem().getId(), dto.getItem().getId());
    }
}