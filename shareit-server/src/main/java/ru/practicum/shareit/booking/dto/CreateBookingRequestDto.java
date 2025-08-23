package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CreateBookingRequestDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
