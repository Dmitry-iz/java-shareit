package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.item.CommentDto;
import ru.practicum.shareit.dto.item.CreateItemRequestDto;
import ru.practicum.shareit.dto.item.UpdateItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplate restTemplate) {
        super(restTemplate, serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> createItem(Long userId, CreateItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, UpdateItemRequestDto requestDto) {
        return patch("/" + itemId, userId, requestDto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUsersItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(String text, Long userId) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}