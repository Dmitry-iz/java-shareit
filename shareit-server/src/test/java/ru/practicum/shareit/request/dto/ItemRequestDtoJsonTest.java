package ru.practicum.shareit.request.dto;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;


import jakarta.validation.Validator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Autowired
    private JacksonTester<ItemRequestWithItemsDto> jsonWithItems;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerializeItemRequestDto() throws IOException {
        ItemRequestDto dto = new ItemRequestDto("Need a drill");

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a drill");
    }

    @Test
    void testDeserializeItemRequestDto() throws IOException {
        String content = "{\"description\":\"Need a drill\"}";

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void testSerializeItemRequestWithItemsDto() throws IOException {
        ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto(
                1L,
                "Need a drill",
                LocalDateTime.of(2023, 1, 1, 12, 0),
                List.of()
        );

        JsonContent<ItemRequestWithItemsDto> result = jsonWithItems.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotEmpty();
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }

    @Test
    void testDeserializeItemRequestWithItemsDto() throws IOException {
        String content = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2023-01-01T12:00:00\",\"items\":[]}";

        ItemRequestWithItemsDto dto = jsonWithItems.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2023, 1, 1, 12, 0));
        assertThat(dto.getItems()).isEmpty();
    }

//    @Test
//    void testItemRequestDtoValidation() {
//        // Valid DTO
//        ItemRequestDto validDto = new ItemRequestDto("Valid description");
//        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(validDto);
//        assertThat(violations).isEmpty();
//
//        // Invalid DTO - blank description
//        ItemRequestDto invalidDto = new ItemRequestDto("");
//        violations = validator.validate(invalidDto);
//        assertThat(violations).hasSize(1);
//        assertThat(violations.iterator().next().getMessage()).contains("cannot be blank");
//
//        // Invalid DTO - null description
//        ItemRequestDto nullDto = new ItemRequestDto(null);
//        violations = validator.validate(nullDto);
//        assertThat(violations).hasSize(1);
//        assertThat(violations.iterator().next().getMessage()).contains("cannot be blank");
//    }
//
//    @Test
//    void testItemRequestWithItemsDtoValidation() {
//        // Valid DTO
//        ItemRequestWithItemsDto validDto = new ItemRequestWithItemsDto(
//                1L,
//                "Valid description",
//                LocalDateTime.now(),
//                List.of()
//        );
//        Set<ConstraintViolation<ItemRequestWithItemsDto>> violations = validator.validate(validDto);
//        assertThat(violations).isEmpty();
//
//        // Invalid DTO - null description
//        ItemRequestWithItemsDto invalidDto = new ItemRequestWithItemsDto(
//                1L,
//                null,
//                LocalDateTime.now(),
//                List.of()
//        );
//        violations = validator.validate(invalidDto);
//        assertThat(violations).hasSize(1);
//        assertThat(violations.iterator().next().getMessage()).contains("cannot be blank");
//    }
}