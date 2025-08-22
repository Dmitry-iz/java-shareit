package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws IOException {
        ItemDto.BookingInfo lastBooking = new ItemDto.BookingInfo(1L, 10L);
        ItemDto.BookingInfo nextBooking = new ItemDto.BookingInfo(2L, 20L);
        CommentDto comment = new CommentDto(1L, "Great item!", "User", null);

        ItemDto itemDto = new ItemDto(
                1L,
                "Item",
                "Description",
                true,
                lastBooking,
                nextBooking,
                List.of(comment)
        );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(10);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(20);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"id\":1,\"name\":\"Item\",\"description\":\"Description\"," +
                "\"available\":true,\"lastBooking\":{\"id\":1,\"bookerId\":10}," +
                "\"nextBooking\":{\"id\":2,\"bookerId\":20}," +
                "\"comments\":[{\"id\":1,\"text\":\"Great item!\",\"authorName\":\"User\"}]}";

        ItemDto result = objectMapper.readValue(content, ItemDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Item");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getLastBooking().getId()).isEqualTo(1L);
        assertThat(result.getLastBooking().getBookerId()).isEqualTo(10L);
        assertThat(result.getNextBooking().getId()).isEqualTo(2L);
        assertThat(result.getNextBooking().getBookerId()).isEqualTo(20L);
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getText()).isEqualTo("Great item!");
    }

//    @Test
//    void testSerializeWithNullValues() throws IOException {
//        ItemDto itemDto = new ItemDto(
//                1L,
//                "Item",
//                "Description",
//                true,
//                null,
//                null,
//                null
//        );
//
//        JsonContent<ItemDto> result = json.write(itemDto);
//
//        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
//        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item");
//        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
//        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
//        assertThat(result).extractingJsonPathValue("$.lastBooking").isNull();
//        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
//        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
//    }
}