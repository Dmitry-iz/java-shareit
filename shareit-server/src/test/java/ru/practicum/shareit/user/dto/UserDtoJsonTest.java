package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<CreateUserRequestDto> createJson;

    @Autowired
    private JacksonTester<UpdateUserRequestDto> updateJson;

    @Autowired
    private JacksonTester<UserDto> userJson;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createUserRequestDtoSerializationTest() throws Exception {
        // Arrange
        CreateUserRequestDto dto = new CreateUserRequestDto();
        dto.setName("Test User");
        dto.setEmail("test@email.com");

        // Act
        JsonContent<CreateUserRequestDto> result = createJson.write(dto);

        // Assert
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@email.com");
    }

    @Test
    void createUserRequestDtoDeserializationTest() throws Exception {
        // Arrange
        String json = "{\"name\":\"Test User\",\"email\":\"test@email.com\"}";

        // Act
        CreateUserRequestDto dto = createJson.parse(json).getObject();

        // Assert
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void updateUserRequestDtoSerializationTest() throws Exception {
        // Arrange
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("Updated User");
        dto.setEmail("updated@email.com");

        // Act
        JsonContent<UpdateUserRequestDto> result = updateJson.write(dto);

        // Assert
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Updated User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("updated@email.com");
    }

    @Test
    void updateUserRequestDtoDeserializationTest() throws Exception {
        // Arrange
        String json = "{\"name\":\"Updated User\",\"email\":\"updated@email.com\"}";

        // Act
        UpdateUserRequestDto dto = updateJson.parse(json).getObject();

        // Assert
        assertThat(dto.getName()).isEqualTo("Updated User");
        assertThat(dto.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void userDtoSerializationTest() throws Exception {
        // Arrange
        UserDto dto = new UserDto(1L, "Test User", "test@email.com");

        // Act
        JsonContent<UserDto> result = userJson.write(dto);

        // Assert
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@email.com");
    }

    @Test
    void userDtoDeserializationTest() throws Exception {
        // Arrange
        String json = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@email.com\"}";

        // Act
        UserDto dto = userJson.parse(json).getObject();

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void userDtoWithNullFieldsSerializationTest() throws Exception {
        // Arrange
        UserDto dto = new UserDto(null, null, null);

        // Act
        JsonContent<UserDto> result = userJson.write(dto);

        // Assert
        assertThat(result).extractingJsonPathValue("$.id").isNull();
        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }
}
