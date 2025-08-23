package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CreateBookingRequestDtoTest {

    @Test
    void testNoArgsConstructor() {
        CreateBookingRequestDto dto = new CreateBookingRequestDto();
        assertThat(dto).isNotNull();
    }

    @Test
    void testSettersAndGetters() {
        CreateBookingRequestDto dto = new CreateBookingRequestDto();
        LocalDateTime start = LocalDateTime.of(2023, 12, 25, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 26, 10, 0);

        dto.setItemId(1L);
        dto.setStart(start);
        dto.setEnd(end);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 25, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 26, 10, 0);

        CreateBookingRequestDto dto1 = new CreateBookingRequestDto();
        dto1.setItemId(1L);
        dto1.setStart(start);
        dto1.setEnd(end);

        CreateBookingRequestDto dto2 = new CreateBookingRequestDto();
        dto2.setItemId(1L);
        dto2.setStart(start);
        dto2.setEnd(end);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testNotEqualsWithDifferentItemId() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 25, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 26, 10, 0);

        CreateBookingRequestDto dto1 = new CreateBookingRequestDto();
        dto1.setItemId(1L);
        dto1.setStart(start);
        dto1.setEnd(end);

        CreateBookingRequestDto dto2 = new CreateBookingRequestDto();
        dto2.setItemId(2L);
        dto2.setStart(start);
        dto2.setEnd(end);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        CreateBookingRequestDto dto = new CreateBookingRequestDto();
        dto.setItemId(1L);

        String toStringResult = dto.toString();
        assertThat(toStringResult).contains("itemId=1");
    }

    @Test
    void testAllNullValues() {
        CreateBookingRequestDto dto = new CreateBookingRequestDto();
        dto.setItemId(null);
        dto.setStart(null);
        dto.setEnd(null);

        assertThat(dto.getItemId()).isNull();
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }

    @Test
    void testDateTimeBoundaries() {
        LocalDateTime minDateTime = LocalDateTime.MIN;
        LocalDateTime maxDateTime = LocalDateTime.MAX;

        CreateBookingRequestDto dto = new CreateBookingRequestDto();
        dto.setStart(minDateTime);
        dto.setEnd(maxDateTime);

        assertThat(dto.getStart()).isEqualTo(minDateTime);
        assertThat(dto.getEnd()).isEqualTo(maxDateTime);
    }

    @Test
    void testPartialNullValues() {
        CreateBookingRequestDto dto = new CreateBookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(null);
        dto.setEnd(LocalDateTime.now());

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNotNull();
    }
}