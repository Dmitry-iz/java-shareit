package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toDto_ShouldMapCorrectly() {
        User author = new User(1L, "Author", "author@email.com");
        Comment comment = new Comment(1L, "Great item!", new Item(), author, LocalDateTime.now());

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(author.getName(), result.getAuthorName());
    }

    @Test
    void fromDto_ShouldMapCorrectly() {
        CommentDto dto = new CommentDto(null, "Test comment", null, null);
        Item item = new Item();
        User author = new User(1L, "Author", "author@email.com");

        Comment result = mapper.fromDto(dto, item, author);

        assertNotNull(result);
        assertEquals(dto.getText(), result.getText());
        assertEquals(item, result.getItem());
        assertEquals(author, result.getAuthor());
        assertNotNull(result.getCreated());
    }

    @Test
    void fromDto_WithNullParameters_ShouldReturnNull() {
        assertNull(mapper.fromDto(null, null, null));
    }
}
