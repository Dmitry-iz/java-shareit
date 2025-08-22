package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.user.CreateUserRequestDto;
import ru.practicum.shareit.dto.user.UpdateUserRequestDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplate rest) {
        super(rest, serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> createUser(CreateUserRequestDto requestDto) {
        return post("", null, requestDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UpdateUserRequestDto requestDto) {
        // Правильный вызов PATCH метода
        return patch("/" + userId, userId, requestDto);
    }

    public ResponseEntity<Object> getUser(Long userId) {
        return get("/" + userId, userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("", null);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId, userId);
    }
}
