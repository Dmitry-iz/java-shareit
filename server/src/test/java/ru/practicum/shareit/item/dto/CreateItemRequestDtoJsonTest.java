package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<CreateItemRequestDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws IOException {
        CreateItemRequestDto dto = new CreateItemRequestDto("Item", "Description", true, 1L);

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"name\":\"Item\",\"description\":\"Description\",\"available\":true,\"requestId\":1}";

        CreateItemRequestDto result = objectMapper.readValue(content, CreateItemRequestDto.class);

        assertThat(result.getName()).isEqualTo("Item");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(1L);
    }

    @Test
    void testDeserializeWithNullRequestId() throws IOException {
        String content = "{\"name\":\"Item\",\"description\":\"Description\",\"available\":true}";

        CreateItemRequestDto result = objectMapper.readValue(content, CreateItemRequestDto.class);

        assertThat(result.getName()).isEqualTo("Item");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isNull();
    }
}
