package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2023, 12, 25, 10, 0));
        dto.setEnd(LocalDateTime.of(2023, 12, 26, 10, 0));
        dto.setStatus(BookingStatus.WAITING);

        BookingDto.BookerDto booker = new BookingDto.BookerDto();
        booker.setId(2L);
        booker.setName("John Doe");
        dto.setBooker(booker);

        BookingDto.ItemDto item = new BookingDto.ItemDto();
        item.setId(3L);
        item.setName("Test Item");
        dto.setItem(item);

        String result = objectMapper.writeValueAsString(dto);

        assertThat(result).contains("\"id\":1");
        assertThat(result).contains("\"status\":\"WAITING\"");
        assertThat(result).contains("\"name\":\"John Doe\"");
        assertThat(result).contains("\"name\":\"Test Item\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"start\":\"2023-12-25T10:00:00\",\"end\":\"2023-12-26T10:00:00\"," +
                "\"status\":\"APPROVED\",\"booker\":{\"id\":2,\"name\":\"John Doe\"}," +
                "\"item\":{\"id\":3,\"name\":\"Test Item\"}}";

        BookingDto result = objectMapper.readValue(content, BookingDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
        assertThat(result.getBooker().getName()).isEqualTo("John Doe");
        assertThat(result.getItem().getId()).isEqualTo(3L);
        assertThat(result.getItem().getName()).isEqualTo("Test Item");
    }

    @Test
    void testSerializeWithNullFields() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2023, 12, 25, 10, 0));
        dto.setEnd(LocalDateTime.of(2023, 12, 26, 10, 0));
        dto.setStatus(BookingStatus.WAITING);
        dto.setBooker(null);
        dto.setItem(null);

        String jsonStr = json.write(dto).getJson();

        assertThat(jsonStr).contains("\"id\":1");
        assertThat(jsonStr).contains("\"status\":\"WAITING\"");
    }

    @Test
    void testDeserializeWithDifferentStatus() throws Exception {
        String content = "{\"id\":1,\"start\":\"2023-12-25T10:00:00\",\"end\":\"2023-12-26T10:00:00\"," +
                "\"status\":\"CANCELLED\",\"booker\":{\"id\":2,\"name\":\"John\"}," +
                "\"item\":{\"id\":3,\"name\":\"Item\"}}";

        BookingDto result = objectMapper.readValue(content, BookingDto.class);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void testSerializeWithDifferentTimeFormats() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2023, 12, 25, 10, 30, 45, 123000000));
        dto.setEnd(LocalDateTime.of(2023, 12, 26, 15, 45, 30, 456000000));
        dto.setStatus(BookingStatus.WAITING);

        String result = objectMapper.writeValueAsString(dto);

        assertThat(result).contains("\"start\":\"2023-12-25T10:30:45.123\"");
        assertThat(result).contains("\"end\":\"2023-12-26T15:45:30.456\"");
    }

    @Test
    void testDeserializeWithDifferentTimeFormats() throws Exception {
        String content = "{\"id\":1,\"start\":\"2023-12-25T10:30:45.123\",\"end\":\"2023-12-26T15:45:30.456\"," +
                "\"status\":\"WAITING\",\"booker\":{\"id\":2,\"name\":\"John Doe\"}," +
                "\"item\":{\"id\":3,\"name\":\"Test Item\"}}";

        BookingDto result = objectMapper.readValue(content, BookingDto.class);

        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2023, 12, 25, 10, 30, 45, 123000000));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2023, 12, 26, 15, 45, 30, 456000000));
    }

    @Test
    void testSerializeWithEmptyInnerObjects() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2023, 12, 25, 10, 0));
        dto.setEnd(LocalDateTime.of(2023, 12, 26, 10, 0));
        dto.setStatus(BookingStatus.WAITING);

        BookingDto.BookerDto booker = new BookingDto.BookerDto();
        booker.setId(null);
        booker.setName("");
        dto.setBooker(booker);

        BookingDto.ItemDto item = new BookingDto.ItemDto();
        item.setId(null);
        item.setName("");
        dto.setItem(item);

        String jsonStr = json.write(dto).getJson();

        assertThat(jsonStr).contains("\"booker\":{\"id\":null,\"name\":\"\"}");
        assertThat(jsonStr).contains("\"item\":{\"id\":null,\"name\":\"\"}");
    }

    @Test
    void testDeserializeWithEmptyInnerObjects() throws Exception {
        String content = "{\"id\":1,\"start\":\"2023-12-25T10:00:00\",\"end\":\"2023-12-26T10:00:00\"," +
                "\"status\":\"WAITING\",\"booker\":{\"id\":null,\"name\":\"\"}," +
                "\"item\":{\"id\":null,\"name\":\"\"}}";

        BookingDto result = objectMapper.readValue(content, BookingDto.class);

        assertThat(result.getBooker().getId()).isNull();
        assertThat(result.getBooker().getName()).isEmpty();
        assertThat(result.getItem().getId()).isNull();
        assertThat(result.getItem().getName()).isEmpty();
    }

    @Test
    void testSerializeWithAllBookingStatusValues() throws Exception {
        for (BookingStatus status : BookingStatus.values()) {
            BookingDto dto = new BookingDto();
            dto.setId(1L);
            dto.setStart(LocalDateTime.of(2023, 12, 25, 10, 0));
            dto.setEnd(LocalDateTime.of(2023, 12, 26, 10, 0));
            dto.setStatus(status);

            String result = objectMapper.writeValueAsString(dto);
            assertThat(result).contains("\"status\":\"" + status.toString() + "\"");
        }
    }

    @Test
    void testDeserializeWithAllBookingStatusValues() throws Exception {
        for (BookingStatus status : BookingStatus.values()) {
            String content = String.format("{\"id\":1,\"start\":\"2023-12-25T10:00:00\",\"end\":\"2023-12-26T10:00:00\"," +
                    "\"status\":\"%s\",\"booker\":{\"id\":2,\"name\":\"John\"}," +
                    "\"item\":{\"id\":3,\"name\":\"Item\"}}", status.toString());

            BookingDto result = objectMapper.readValue(content, BookingDto.class);
            assertThat(result.getStatus()).isEqualTo(status);
        }
    }
}