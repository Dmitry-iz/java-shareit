package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.exception.ItemNotOwnedByUserException;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final Long userId = 1L;
    private final Long bookingId = 1L;

    private CreateBookingRequestDto createValidBookingRequest() {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        return requestDto;
    }

    private BookingDto createBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setStatus(BookingStatus.WAITING);

        BookingDto.BookerDto booker = new BookingDto.BookerDto();
        booker.setId(2L);
        booker.setName("Booker Name");
        dto.setBooker(booker);

        BookingDto.ItemDto item = new BookingDto.ItemDto();
        item.setId(1L);
        item.setName("Test Item");
        dto.setItem(item);

        return dto;
    }

    @Test
    void create_ValidRequest_ReturnsBooking() throws Exception {
        CreateBookingRequestDto requestDto = createValidBookingRequest();
        BookingDto responseDto = createBookingDto();

        when(bookingService.create(anyLong(), any(CreateBookingRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.status").value(responseDto.getStatus().name()));
    }

    @Test
    void approve_ValidRequest_ReturnsApprovedBooking() throws Exception {
        BookingDto responseDto = createBookingDto();
        responseDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void cancel_ValidRequest_ReturnsCancelledBooking() throws Exception {
        BookingDto responseDto = createBookingDto();
        responseDto.setStatus(BookingStatus.CANCELLED);

        when(bookingService.cancel(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/{bookingId}/cancel", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getById_ValidRequest_ReturnsBooking() throws Exception {
        BookingDto responseDto = createBookingDto();

        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));
    }

    @Test
    void getAllByUser_ValidRequest_ReturnsBookings() throws Exception {
        BookingDto bookingDto = createBookingDto();
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingService.getAllByUser(anyLong(), eq("ALL")))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }

    @Test
    void getAllByOwner_ValidRequest_ReturnsBookings() throws Exception {
        BookingDto bookingDto = createBookingDto();
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingService.getAllByOwner(anyLong(), eq("ALL")))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }

    @Test
    void create_InvalidDates_ReturnsBadRequest() throws Exception {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(2));
        requestDto.setEnd(LocalDateTime.now().plusDays(1));

        when(bookingService.create(anyLong(), any(CreateBookingRequestDto.class)))
                .thenThrow(new BadRequestException("End date must be after start date"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByUser_InvalidState_ReturnsBadRequest() throws Exception {
        when(bookingService.getAllByUser(anyLong(), eq("INVALID_STATE")))
                .thenThrow(new BadRequestException("Unknown state: INVALID_STATE"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "INVALID_STATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_MissingFields_ShouldReturnBadRequest() throws Exception {
        CreateBookingRequestDto dto = new CreateBookingRequestDto();
        dto.setItemId(null);
        dto.setStart(null);
        dto.setEnd(null);

        when(bookingService.create(anyLong(), any())).thenThrow(new BadRequestException("Start and end dates must be provided"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approve_InvalidStatus_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "notBoolean"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approve_NotOwner_ShouldReturnForbidden() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new ItemNotOwnedByUserException("User is not the owner"));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 999L)
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }
}