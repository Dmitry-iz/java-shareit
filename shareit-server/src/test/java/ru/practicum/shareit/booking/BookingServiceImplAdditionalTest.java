//package ru.practicum.shareit.booking;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Sort;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.booking.model.BookingStatus;
//import ru.practicum.shareit.booking.repository.BookingRepository;
//import ru.practicum.shareit.booking.service.BookingServiceImpl;
//import ru.practicum.shareit.exception.BadRequestException;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class BookingServiceImplAdditionalTest {
//
//    @Mock
//    private BookingRepository bookingRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private ItemRepository itemRepository;
//
//    @InjectMocks
//    private BookingServiceImpl bookingService;
//
//    @Test
//    void create_WithStartDateInPast_ShouldThrowException() {
//        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
//        requestDto.setItemId(1L);
//        requestDto.setStart(LocalDateTime.now().minusDays(1));
//        requestDto.setEnd(LocalDateTime.now().plusDays(1));
//
//        User user = new User();
//        user.setId(1L);
//        Item item = new Item();
//        item.setId(1L);
//        item.setAvailable(true);
//        User owner = new User();
//        owner.setId(2L);
//        item.setOwner(owner);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//
//        assertThrows(BadRequestException.class, () ->
//                bookingService.create(1L, requestDto));
//    }
//
//    @Test
//    void getAllByUser_WithWaitingState_ShouldReturnWaitingBookings() {
//        User user = new User();
//        user.setId(1L);
//        Booking booking = new Booking();
//        booking.setStatus(BookingStatus.WAITING);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(bookingRepository.findByBookerIdAndStatus(eq(1L), eq(BookingStatus.WAITING), any(Sort.class)))
//                .thenReturn(List.of(booking));
//
//        List<BookingDto> result = bookingService.getAllByUser(1L, "WAITING");
//
//        assertEquals(1, result.size());
//        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
//    }
//
//    @Test
//    void getAllByOwner_WithRejectedState_ShouldReturnRejectedBookings() {
//        User user = new User();
//        user.setId(1L);
//        Booking booking = new Booking();
//        booking.setStatus(BookingStatus.REJECTED);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(bookingRepository.findByItemOwnerIdAndStatus(eq(1L), eq(BookingStatus.REJECTED), any(Sort.class)))
//                .thenReturn(List.of(booking));
//
//        List<BookingDto> result = bookingService.getAllByOwner(1L, "REJECTED");
//
//        assertEquals(1, result.size());
//        assertEquals(BookingStatus.REJECTED, result.get(0).getStatus());
//    }
//
//    @Test
//    void getAllByUser_WithCurrentState_ShouldReturnCurrentBookings() {
//        User user = new User();
//        user.setId(1L);
//        Booking booking = new Booking();
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(eq(1L), any(), any(), any(Sort.class)))
//                .thenReturn(List.of(booking));
//
//        List<BookingDto> result = bookingService.getAllByUser(1L, "CURRENT");
//
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void getAllByUser_WithFutureState_ShouldReturnFutureBookings() {
//        User user = new User();
//        user.setId(1L);
//        Booking booking = new Booking();
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(bookingRepository.findByBookerIdAndStartAfter(eq(1L), any(), any(Sort.class)))
//                .thenReturn(List.of(booking));
//
//        List<BookingDto> result = bookingService.getAllByUser(1L, "FUTURE");
//
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void getAllByUser_WithPastState_ShouldReturnPastBookings() {
//        User user = new User();
//        user.setId(1L);
//        Booking booking = new Booking();
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(bookingRepository.findByBookerIdAndEndBefore(eq(1L), any(), any(Sort.class)))
//                .thenReturn(List.of(booking));
//
//        List<BookingDto> result = bookingService.getAllByUser(1L, "PAST");
//
//        assertEquals(1, result.size());
//    }
//}
