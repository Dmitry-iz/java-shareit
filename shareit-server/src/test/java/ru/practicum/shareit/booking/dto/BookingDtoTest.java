package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingDtoTest {

    @Test
    void testAllArgsConstructor() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 25, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 26, 10, 0);

        BookingDto.BookerDto booker = new BookingDto.BookerDto(1L, "John Doe");
        BookingDto.ItemDto item = new BookingDto.ItemDto(1L, "Test Item");
        BookingDto dto = new BookingDto(1L, start, end, BookingStatus.WAITING, booker, item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getBooker()).isEqualTo(booker);
        assertThat(dto.getItem()).isEqualTo(item);
    }

    @Test
    void testNoArgsConstructor() {
        BookingDto dto = new BookingDto();
        assertThat(dto).isNotNull();
    }

    @Test
    void testSettersAndGetters() {
        BookingDto dto = new BookingDto();
        LocalDateTime start = LocalDateTime.of(2023, 12, 25, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 26, 10, 0);

        dto.setId(1L);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setStatus(BookingStatus.APPROVED);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testBookerDtoAllArgsConstructor() {
        BookingDto.BookerDto booker = new BookingDto.BookerDto(1L, "Test User");

        assertThat(booker.getId()).isEqualTo(1L);
        assertThat(booker.getName()).isEqualTo("Test User");
    }

    @Test
    void testBookerDtoSettersAndGetters() {
        BookingDto.BookerDto booker = new BookingDto.BookerDto();
        booker.setId(1L);
        booker.setName("Test User");

        assertThat(booker.getId()).isEqualTo(1L);
        assertThat(booker.getName()).isEqualTo("Test User");
    }

    @Test
    void testItemDtoAllArgsConstructor() {
        BookingDto.ItemDto item = new BookingDto.ItemDto(1L, "Test Item");

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Test Item");
    }

    @Test
    void testItemDtoSettersAndGetters() {
        BookingDto.ItemDto item = new BookingDto.ItemDto();
        item.setId(1L);
        item.setName("Test Item");

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Test Item");
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 25, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 26, 10, 0);

        BookingDto dto1 = new BookingDto(1L, start, end, BookingStatus.WAITING, null, null);
        BookingDto dto2 = new BookingDto(1L, start, end, BookingStatus.WAITING, null, null);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testNotEqualsWithDifferentId() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 25, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 26, 10, 0);

        BookingDto dto1 = new BookingDto(1L, start, end, BookingStatus.WAITING, null, null);
        BookingDto dto2 = new BookingDto(2L, start, end, BookingStatus.WAITING, null, null);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(BookingStatus.WAITING);

        String toStringResult = dto.toString();
        assertThat(toStringResult).contains("id=1");
        assertThat(toStringResult).contains("status=WAITING");
    }

    @Test
    void testDateTimeBoundaries() {
        LocalDateTime minDateTime = LocalDateTime.MIN;
        LocalDateTime maxDateTime = LocalDateTime.MAX;

        BookingDto dto = new BookingDto();
        dto.setStart(minDateTime);
        dto.setEnd(maxDateTime);

        assertThat(dto.getStart()).isEqualTo(minDateTime);
        assertThat(dto.getEnd()).isEqualTo(maxDateTime);
    }

    @Test
    void testNullValues() {
        BookingDto dto = new BookingDto();
        dto.setBooker(null);
        dto.setItem(null);

        assertThat(dto.getBooker()).isNull();
        assertThat(dto.getItem()).isNull();
    }
}