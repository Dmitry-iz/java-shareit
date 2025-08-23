package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.CreateBookingRequestDto;
import ru.practicum.shareit.booking.exception.BookingAccessDeniedException;
import ru.practicum.shareit.booking.exception.BookingAlreadyProcessedException;
import ru.practicum.shareit.booking.exception.BookingOwnItemException;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
class BookingServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Test
    void createBooking_ShouldCreateBookingSuccessfully() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(item2.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(3));
        requestDto.setEnd(LocalDateTime.now().plusDays(4));

        BookingDto result = bookingService.create(user1.getId(), requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(item2.getId());
        assertThat(result.getBooker().getId()).isEqualTo(user1.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void createBooking_OwnItem_ShouldThrowException() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(item1.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(3));
        requestDto.setEnd(LocalDateTime.now().plusDays(4));

        assertThrows(BookingOwnItemException.class, () ->
                bookingService.create(user1.getId(), requestDto)
        );
    }

    @Test
    void createBooking_UnavailableItem_ShouldThrowException() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(item3.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(3));
        requestDto.setEnd(LocalDateTime.now().plusDays(4));

        assertThrows(RuntimeException.class, () ->
                bookingService.create(user1.getId(), requestDto)
        );
    }

    @Test
    void approveBooking_ShouldApproveSuccessfully() {
        BookingDto result = bookingService.approve(user1.getId(), booking2.getId(), true);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_ShouldRejectSuccessfully() {
        BookingDto result = bookingService.approve(user1.getId(), booking2.getId(), false);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveBooking_ByNonOwner_ShouldThrowException() {
        assertThrows(RuntimeException.class, () ->
                bookingService.approve(user2.getId(), booking2.getId(), true)
        );
    }

    @Test
    void approveBooking_AlreadyProcessed_ShouldThrowException() {
        assertThrows(BookingAlreadyProcessedException.class, () ->
                bookingService.approve(user1.getId(), booking1.getId(), true)
        );
    }

    @Test
    void cancelBooking_ShouldCancelSuccessfully() {
        BookingDto result = bookingService.cancel(user3.getId(), booking2.getId());

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_ByNonBooker_ShouldThrowException() {
        assertThrows(BookingAccessDeniedException.class, () ->
                bookingService.cancel(user1.getId(), booking2.getId())
        );
    }

    @Test
    void getById_AsOwner_ShouldReturnBooking() {
        BookingDto result = bookingService.getById(user1.getId(), booking1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking1.getId());
    }

    @Test
    void getById_AsBooker_ShouldReturnBooking() {
        BookingDto result = bookingService.getById(user2.getId(), booking1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking1.getId());
    }

    @Test
    void getById_ByUnauthorizedUser_ShouldThrowException() {
        assertThrows(BookingAccessDeniedException.class, () ->
                bookingService.getById(user3.getId(), booking1.getId())
        );
    }

    @Test
    void getAllByUser_ShouldReturnUserBookings() {
        List<BookingDto> result = bookingService.getAllByUser(user2.getId(), "ALL");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(booking1.getId());
    }

    @Test
    void getAllByOwner_ShouldReturnOwnerBookings() {
        List<BookingDto> result = bookingService.getAllByOwner(user1.getId(), "ALL");

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllByUser_WithStateFilter_ShouldReturnFilteredBookings() {
        List<BookingDto> past = bookingService.getAllByUser(user2.getId(), "PAST");
        List<BookingDto> future = bookingService.getAllByUser(user3.getId(), "FUTURE");
        List<BookingDto> waiting = bookingService.getAllByUser(user3.getId(), "WAITING");

        assertThat(past).hasSize(1);
        assertThat(future).hasSize(1);
        assertThat(waiting).hasSize(1);
    }
}
