package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws IOException {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Great item!");
        dto.setAuthorName("John");
        dto.setCreated(LocalDateTime.of(2023, 1, 1, 12, 0));

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-01T12:00:00");
    }

    @Test
    void testDeserialize() throws IOException {
        String content = """
                {
                    "id": 1,
                    "text": "Nice!",
                    "authorName": "Alice",
                    "created": "2023-01-01T12:00:00"
                }
                """;

        CommentDto result = objectMapper.readValue(content, CommentDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Nice!");
        assertThat(result.getAuthorName()).isEqualTo("Alice");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2023, 1, 1, 12, 0));
    }
}
