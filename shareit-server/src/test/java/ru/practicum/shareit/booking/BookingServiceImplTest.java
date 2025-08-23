package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.exception.BookingAccessDeniedException;
import ru.practicum.shareit.booking.exception.BookingAlreadyProcessedException;
import ru.practicum.shareit.booking.exception.BookingOwnItemException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Long userId = 1L;
    private final Long ownerId = 2L;
    private final Long bookingId = 1L;
    private final Long itemId = 1L;

    @Test
    void create_ValidRequest_ReturnsBooking() {
        CreateBookingRequestDto requestDto = createValidBookingRequest();
        User booker = createUser(userId, "booker@email.com");
        User owner = createUser(ownerId, "owner@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);
        BookingDto expectedDto = createBookingDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsApprovedBookingsForItemBetweenDates(anyLong(), any(), any()))
                .thenReturn(false);
        when(bookingMapper.fromCreateDto(any(), any(), any(), any())).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(expectedDto);

        BookingDto result = bookingService.create(userId, requestDto);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_UserNotFound_ThrowsException() {
        CreateBookingRequestDto requestDto = createValidBookingRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.create(userId, requestDto));
    }

    @Test
    void create_ItemNotFound_ThrowsException() {
        CreateBookingRequestDto requestDto = createValidBookingRequest();
        User booker = createUser(userId, "booker@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> bookingService.create(userId, requestDto));
    }

    @Test
    void create_ItemNotAvailable_ThrowsException() {
        CreateBookingRequestDto requestDto = createValidBookingRequest();
        User booker = createUser(userId, "booker@email.com");
        User owner = createUser(ownerId, "owner@email.com");
        Item item = createUnavailableItem(owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.create(userId, requestDto));
    }

    @Test
    void create_BookOwnItem_ThrowsException() {
        CreateBookingRequestDto requestDto = createValidBookingRequest();
        User owner = createUser(userId, "owner@email.com");
        Item item = createAvailableItem(owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BookingOwnItemException.class, () -> bookingService.create(userId, requestDto));
    }

    @Test
    void create_DateConflict_ThrowsException() {
        CreateBookingRequestDto requestDto = createValidBookingRequest();
        User booker = createUser(userId, "booker@email.com");
        User owner = createUser(ownerId, "owner@email.com");
        Item item = createAvailableItem(owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsApprovedBookingsForItemBetweenDates(anyLong(), any(), any()))
                .thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookingService.create(userId, requestDto));
    }

    @Test
    void approve_ValidRequest_ReturnsApprovedBooking() {
        User owner = createUser(ownerId, "owner@email.com");
        User booker = createUser(userId, "booker@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);
        BookingDto expectedDto = createBookingDto();
        expectedDto.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(expectedDto);

        BookingDto result = bookingService.approve(ownerId, bookingId, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approve_NotOwner_ThrowsException() {
        User owner = createUser(ownerId, "owner@email.com");
        User booker = createUser(userId, "booker@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ItemNotOwnedByUserException.class,
                () -> bookingService.approve(userId, bookingId, true));
    }

    @Test
    void approve_AlreadyProcessed_ThrowsException() {
        User owner = createUser(ownerId, "owner@email.com");
        User booker = createUser(userId, "booker@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingAlreadyProcessedException.class,
                () -> bookingService.approve(ownerId, bookingId, true));
    }

    @Test
    void cancel_ValidRequest_ReturnsCancelledBooking() {
        User owner = createUser(ownerId, "owner@email.com");
        User booker = createUser(userId, "booker@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);
        BookingDto expectedDto = createBookingDto();
        expectedDto.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(expectedDto);

        BookingDto result = bookingService.cancel(userId, bookingId);

        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancel_NotBooker_ThrowsException() {
        User owner = createUser(ownerId, "owner@email.com");
        User booker = createUser(userId, "booker@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingAccessDeniedException.class,
                () -> bookingService.cancel(ownerId, bookingId));
    }

    @Test
    void getById_ValidRequest_ReturnsBooking() {
        User owner = createUser(ownerId, "owner@email.com");
        User booker = createUser(userId, "booker@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);
        BookingDto expectedDto = createBookingDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any())).thenReturn(expectedDto);

        BookingDto result = bookingService.getById(userId, bookingId);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
    }

    @Test
    void getById_NotAuthorized_ThrowsException() {
        User owner = createUser(ownerId, "owner@email.com");
        User booker = createUser(userId, "booker@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);
        User unauthorizedUser = createUser(3L, "unauthorized@email.com");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingAccessDeniedException.class,
                () -> bookingService.getById(unauthorizedUser.getId(), bookingId));
    }

    @Test
    void getAllByUser_ValidRequest_ReturnsBookings() {
        User booker = createUser(userId, "booker@email.com");
        List<Booking> bookings = List.of(createBooking(booker, createAvailableItem(createUser(ownerId, "owner@email.com"))));
        BookingDto bookingDto = createBookingDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(userId)).thenReturn(bookings);
        when(bookingMapper.toDto(any())).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getAllByUser(userId, "ALL");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllByOwner_ValidRequest_ReturnsBookings() {
        User owner = createUser(userId, "owner@email.com");
        List<Booking> bookings = List.of(createBooking(createUser(2L, "booker@email.com"), createAvailableItem(owner)));
        BookingDto bookingDto = createBookingDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId)).thenReturn(bookings);
        when(bookingMapper.toDto(any())).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getAllByOwner(userId, "ALL");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    private CreateBookingRequestDto createValidBookingRequest() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(itemId);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        return requestDto;
    }

    private User createUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setName("Test User");
        user.setEmail(email);
        return user;
    }

    private Item createAvailableItem(User owner) {
        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private Item createUnavailableItem(User owner) {
        Item item = createAvailableItem(owner);
        item.setAvailable(false);
        return item;
    }

    private Booking createBooking(User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    private BookingDto createBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(bookingId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setStatus(BookingStatus.WAITING);

        BookingDto.BookerDto booker = new BookingDto.BookerDto();
        booker.setId(userId);
        booker.setName("Booker Name");
        dto.setBooker(booker);

        BookingDto.ItemDto item = new BookingDto.ItemDto();
        item.setId(itemId);
        item.setName("Test Item");
        dto.setItem(item);

        return dto;
    }

    @Test
    void create_WithNullStartDate_ShouldThrowBadRequestException() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(itemId);
        requestDto.setStart(null);
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(BadRequestException.class, () -> bookingService.create(userId, requestDto));

        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).existsApprovedBookingsForItemBetweenDates(anyLong(), any(), any());
    }

    @Test
    void create_WithNullEndDate_ShouldThrowBadRequestException() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(itemId);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(null); // Null end date

        assertThrows(BadRequestException.class, () -> bookingService.create(userId, requestDto));

        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).existsApprovedBookingsForItemBetweenDates(anyLong(), any(), any());
    }

    @Test
    void create_WithStartInPast_ShouldThrowBadRequestException() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(itemId);
        requestDto.setStart(LocalDateTime.now().minusDays(1)); // Past start
        requestDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(BadRequestException.class, () -> bookingService.create(userId, requestDto));

        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).existsApprovedBookingsForItemBetweenDates(anyLong(), any(), any());
    }

    @Test
    void create_WithEqualStartAndEnd_ShouldThrowBadRequestException() {
        LocalDateTime now = LocalDateTime.now();
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(itemId);
        requestDto.setStart(now);
        requestDto.setEnd(now);

        assertThrows(BadRequestException.class, () -> bookingService.create(userId, requestDto));

        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).existsApprovedBookingsForItemBetweenDates(anyLong(), any(), any());
    }

    @Test
    void getAllByOwner_WithDifferentStates_ShouldReturnFilteredResults() {
        User owner = createUser(userId, "owner@email.com");
        LocalDateTime now = LocalDateTime.now();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        when(bookingRepository.findByItemOwnerIdAndStatus(eq(userId), eq(BookingStatus.WAITING), any(Sort.class)))
                .thenReturn(List.of(createBooking(createUser(2L, "booker@email.com"), createAvailableItem(owner))));

        List<BookingDto> waitingResult = bookingService.getAllByOwner(userId, "WAITING");
        assertFalse(waitingResult.isEmpty());

        when(bookingRepository.findByItemOwnerIdAndStatus(eq(userId), eq(BookingStatus.REJECTED), any(Sort.class)))
                .thenReturn(List.of(createBooking(createUser(2L, "booker@email.com"), createAvailableItem(owner))));

        List<BookingDto> rejectedResult = bookingService.getAllByOwner(userId, "REJECTED");
        assertFalse(rejectedResult.isEmpty());

        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId))
                .thenReturn(List.of(createBooking(createUser(2L, "booker@email.com"), createAvailableItem(owner))));

        List<BookingDto> invalidResult = bookingService.getAllByOwner(userId, "INVALID_STATE");
        assertFalse(invalidResult.isEmpty());
    }

    @Test
    void getAllByOwner_WithEmptyResults_ShouldReturnEmptyList() {
        User owner = createUser(userId, "owner@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId)).thenReturn(List.of());

        List<BookingDto> result = bookingService.getAllByOwner(userId, "ALL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllByOwner_WithFutureState_ShouldReturnFutureBookings() {
        User owner = createUser(userId, "owner@email.com");
        LocalDateTime now = LocalDateTime.now();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartAfter(eq(userId), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(createBooking(createUser(2L, "booker@email.com"), createAvailableItem(owner))));

        List<BookingDto> result = bookingService.getAllByOwner(userId, "FUTURE");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllByOwner_WithPastState_ShouldReturnPastBookings() {
        User owner = createUser(userId, "owner@email.com");
        LocalDateTime now = LocalDateTime.now();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndEndBefore(eq(userId), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(createBooking(createUser(2L, "booker@email.com"), createAvailableItem(owner))));

        List<BookingDto> result = bookingService.getAllByOwner(userId, "PAST");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllByOwner_WithCurrentState_ShouldReturnCurrentBookings() {
        User owner = createUser(userId, "owner@email.com");
        LocalDateTime now = LocalDateTime.now();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(createBooking(createUser(2L, "booker@email.com"), createAvailableItem(owner))));

        List<BookingDto> result = bookingService.getAllByOwner(userId, "CURRENT");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllByUser_WithDifferentStates_ShouldReturnFilteredResults() {
        User booker = createUser(userId, "booker@email.com");
        LocalDateTime now = LocalDateTime.now();

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        when(bookingRepository.findByBookerIdAndStatus(eq(userId), eq(BookingStatus.WAITING), any(Sort.class)))
                .thenReturn(List.of(createBooking(booker, createAvailableItem(createUser(ownerId, "owner@email.com")))));

        List<BookingDto> waitingResult = bookingService.getAllByUser(userId, "WAITING");
        assertFalse(waitingResult.isEmpty());

        when(bookingRepository.findByBookerIdAndStatus(eq(userId), eq(BookingStatus.REJECTED), any(Sort.class)))
                .thenReturn(List.of(createBooking(booker, createAvailableItem(createUser(ownerId, "owner@email.com")))));

        List<BookingDto> rejectedResult = bookingService.getAllByUser(userId, "REJECTED");
        assertFalse(rejectedResult.isEmpty());

        when(bookingRepository.findByBookerIdAndStartAfter(eq(userId), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(createBooking(booker, createAvailableItem(createUser(ownerId, "owner@email.com")))));

        List<BookingDto> futureResult = bookingService.getAllByUser(userId, "FUTURE");
        assertFalse(futureResult.isEmpty());

        when(bookingRepository.findByBookerIdAndEndBefore(eq(userId), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(createBooking(booker, createAvailableItem(createUser(ownerId, "owner@email.com")))));

        List<BookingDto> pastResult = bookingService.getAllByUser(userId, "PAST");
        assertFalse(pastResult.isEmpty());

        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(createBooking(booker, createAvailableItem(createUser(ownerId, "owner@email.com")))));

        List<BookingDto> currentResult = bookingService.getAllByUser(userId, "CURRENT");
        assertFalse(currentResult.isEmpty());
    }

    @Test
    void cancel_AlreadyProcessed_ThrowsException() {
        User owner = createUser(ownerId, "owner@email.com");
        User booker = createUser(userId, "booker@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingAlreadyProcessedException.class,
                () -> bookingService.cancel(userId, bookingId));
    }

    @Test
    void approve_WithFalse_ShouldRejectBooking() {
        User owner = createUser(ownerId, "owner@email.com");
        User booker = createUser(userId, "booker@email.com");
        Item item = createAvailableItem(owner);
        Booking booking = createBooking(booker, item);
        BookingDto expectedDto = createBookingDto();
        expectedDto.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(expectedDto);

        BookingDto result = bookingService.approve(ownerId, bookingId, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void getAllByUser_WithInvalidState_ShouldReturnAll() {
        User booker = createUser(userId, "booker@email.com");
        List<Booking> bookings = List.of(createBooking(booker, createAvailableItem(createUser(ownerId, "owner@email.com"))));

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(userId)).thenReturn(bookings);
        when(bookingMapper.toDto(any())).thenReturn(createBookingDto());

        List<BookingDto> result = bookingService.getAllByUser(userId, "INVALID_STATE");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllByUser_WithEmptyResults_ShouldReturnEmptyList() {
        User booker = createUser(userId, "booker@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(userId)).thenReturn(List.of());

        List<BookingDto> result = bookingService.getAllByUser(userId, "ALL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
