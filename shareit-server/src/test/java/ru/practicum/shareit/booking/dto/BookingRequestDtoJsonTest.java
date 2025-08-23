package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.CreateBookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<CreateBookingRequestDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        CreateBookingRequestDto dto = new CreateBookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2023, 12, 25, 10, 0));
        dto.setEnd(LocalDateTime.of(2023, 12, 26, 10, 0));

        JsonContent<CreateBookingRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-12-25T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-26T10:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"itemId\":1,\"start\":\"2023-12-25T10:00:00\",\"end\":\"2023-12-26T10:00:00\"}";

        CreateBookingRequestDto result = objectMapper.readValue(content, CreateBookingRequestDto.class);

        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2023, 12, 25, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2023, 12, 26, 10, 0));
    }

    @Test
    void testDeserializeWithMissingFields() throws Exception {
        String content = "{\"start\":\"2023-12-25T10:00:00\",\"end\":\"2023-12-26T10:00:00\"}";

        CreateBookingRequestDto result = objectMapper.readValue(content, CreateBookingRequestDto.class);

        assertThat(result.getItemId()).isNull();
        assertThat(result.getStart()).isNotNull();
        assertThat(result.getEnd()).isNotNull();
    }

    @Test
    void testSerializeWithNullFields() throws Exception {
        CreateBookingRequestDto dto = new CreateBookingRequestDto();
        dto.setItemId(null);
        dto.setStart(LocalDateTime.of(2023, 12, 25, 10, 0));
        dto.setEnd(LocalDateTime.of(2023, 12, 26, 10, 0));

        String jsonStr = json.write(dto).getJson();

        assertThat(jsonStr).contains("\"start\":\"2023-12-25T10:00:00\"");
        assertThat(jsonStr).contains("\"end\":\"2023-12-26T10:00:00\"");
    }

    @Test
    void testDeserializeWithMissingItemId() throws Exception {
        String content = "{\"start\":\"2023-12-25T10:00:00\",\"end\":\"2023-12-26T10:00:00\"}";

        CreateBookingRequestDto dto = objectMapper.readValue(content, CreateBookingRequestDto.class);

        assertThat(dto.getItemId()).isNull();
    }
}