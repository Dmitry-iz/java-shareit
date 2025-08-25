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
        CreateUserRequestDto dto = new CreateUserRequestDto();
        dto.setName("Test User");
        dto.setEmail("test@email.com");

        JsonContent<CreateUserRequestDto> result = createJson.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@email.com");
    }

    @Test
    void createUserRequestDtoDeserializationTest() throws Exception {
        String json = "{\"name\":\"Test User\",\"email\":\"test@email.com\"}";

        CreateUserRequestDto dto = createJson.parse(json).getObject();

        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void updateUserRequestDtoSerializationTest() throws Exception {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setName("Updated User");
        dto.setEmail("updated@email.com");

        JsonContent<UpdateUserRequestDto> result = updateJson.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Updated User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("updated@email.com");
    }

    @Test
    void updateUserRequestDtoDeserializationTest() throws Exception {
        String json = "{\"name\":\"Updated User\",\"email\":\"updated@email.com\"}";

        UpdateUserRequestDto dto = updateJson.parse(json).getObject();

        assertThat(dto.getName()).isEqualTo("Updated User");
        assertThat(dto.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void userDtoSerializationTest() throws Exception {
        UserDto dto = new UserDto(1L, "Test User", "test@email.com");

        JsonContent<UserDto> result = userJson.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@email.com");
    }

    @Test
    void userDtoDeserializationTest() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@email.com\"}";

        UserDto dto = userJson.parse(json).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void userDtoWithNullFieldsSerializationTest() throws Exception {
        UserDto dto = new UserDto(null, null, null);


        JsonContent<UserDto> result = userJson.write(dto);

        assertThat(result).extractingJsonPathValue("$.id").isNull();
        assertThat(result).extractingJsonPathValue("$.name").isNull();
        assertThat(result).extractingJsonPathValue("$.email").isNull();
    }
}
