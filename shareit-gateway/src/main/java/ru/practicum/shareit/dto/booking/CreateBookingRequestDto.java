//package ru.practicum.shareit.dto.booking;
//
//import jakarta.validation.constraints.NotNull;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Data
//@NoArgsConstructor
//public class CreateBookingRequestDto {
//    @NotNull(message = "Item ID cannot be null")
//    private Long itemId;
//
//    //@FutureOrPresent(message = "Start date must be in present or future")
//    @NotNull(message = "Start date cannot be null")
//    private LocalDateTime start;
//
//    //@Future(message = "End date must be in future")
//    @NotNull(message = "End date cannot be null")
//    private LocalDateTime end;
//}
//
