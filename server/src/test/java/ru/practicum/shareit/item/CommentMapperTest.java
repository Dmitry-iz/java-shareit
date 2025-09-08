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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toDto_ShouldMapCorrectly() {
        User author = new User(1L, "Author", "author@email.com");
        Item item = new Item(1L, "Item", "Description", true, author, null);
        LocalDateTime created = LocalDateTime.of(2023, 12, 1, 10, 30);
        Comment comment = new Comment(1L, "Great item!", item, author, created);

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(author.getName(), result.getAuthorName());
        assertEquals(created, result.getCreated());
    }

    @Test
    void toDto_WithNullAuthor_ShouldHandleGracefully() {
        Comment comment = new Comment(1L, "Great item!", new Item(), null, LocalDateTime.now());

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertNull(result.getAuthorName());
    }

    @Test
    void toDto_WithNullAuthorName_ShouldHandleGracefully() {
        User author = new User(1L, null, "author@email.com");
        Comment comment = new Comment(1L, "Great item!", new Item(), author, LocalDateTime.now());

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertNull(result.getAuthorName());
    }

    @Test
    void toDto_WithNullComment_ShouldReturnNull() {
        CommentDto result = mapper.toDto(null);
        assertNull(result);
    }

    @Test
    void fromDto_ShouldMapCorrectly() {
        CommentDto dto = new CommentDto(null, "Test comment", null, null);
        Item item = new Item(1L, "Item", "Description", true, new User(), null);
        User author = new User(1L, "Author", "author@email.com");

        Comment result = mapper.fromDto(dto, item, author);

        assertNotNull(result);
        assertEquals(dto.getText(), result.getText());
        assertEquals(item, result.getItem());
        assertEquals(author, result.getAuthor());
        assertNotNull(result.getCreated());
        assertTrue(result.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(result.getCreated().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void fromDto_WithNullDtoButOtherParametersNotNull_ShouldCreateCommentWithNullText() {
        Item item = new Item();
        User author = new User();

        Comment result = mapper.fromDto(null, item, author);

        assertNotNull(result);
        assertEquals(item, result.getItem());
        assertEquals(author, result.getAuthor());
        assertNull(result.getText());
        assertNotNull(result.getCreated());
    }

    @Test
    void fromDto_WithNullItemButOtherParametersNotNull_ShouldCreateCommentWithNullItem() {
        CommentDto dto = new CommentDto(null, "Test comment", null, null);
        User author = new User();

        Comment result = mapper.fromDto(dto, null, author);

        assertNotNull(result);
        assertEquals(dto.getText(), result.getText());
        assertEquals(author, result.getAuthor());
        assertNull(result.getItem());
        assertNotNull(result.getCreated());
    }

    @Test
    void fromDto_WithNullAuthorButOtherParametersNotNull_ShouldCreateCommentWithNullAuthor() {
        CommentDto dto = new CommentDto(null, "Test comment", null, null);
        Item item = new Item();

        Comment result = mapper.fromDto(dto, item, null);

        assertNotNull(result);
        assertEquals(dto.getText(), result.getText());
        assertEquals(item, result.getItem());
        assertNull(result.getAuthor());
        assertNotNull(result.getCreated());
    }

    @Test
    void fromDto_WithPartialNullParameters_ShouldCreateComments() {
        CommentDto dto = new CommentDto(null, "Test comment", null, null);

        Comment result1 = mapper.fromDto(dto, null, null);
        Comment result2 = mapper.fromDto(null, new Item(), null);
        Comment result3 = mapper.fromDto(null, null, new User());

        assertNotNull(result1);
        assertEquals(dto.getText(), result1.getText());
        assertNull(result1.getItem());
        assertNull(result1.getAuthor());

        assertNotNull(result2);
        assertNull(result2.getText());
        assertNotNull(result2.getItem());
        assertNull(result2.getAuthor());

        assertNotNull(result3);
        assertNull(result3.getText());
        assertNull(result3.getItem());
        assertNotNull(result3.getAuthor());
    }

    @Test
    void fromDto_WithEmptyDtoText_ShouldMapCorrectly() {
        CommentDto dto = new CommentDto(null, "", null, null);
        Item item = new Item();
        User author = new User(1L, "Author", "author@email.com");

        Comment result = mapper.fromDto(dto, item, author);

        assertNotNull(result);
        assertEquals("", result.getText());
        assertEquals(item, result.getItem());
        assertEquals(author, result.getAuthor());
        assertNotNull(result.getCreated());
    }

    @Test
    void fromDto_WithNullDtoText_ShouldMapCorrectly() {
        CommentDto dto = new CommentDto(null, null, null, null);
        Item item = new Item();
        User author = new User(1L, "Author", "author@email.com");

        Comment result = mapper.fromDto(dto, item, author);

        assertNotNull(result);
        assertNull(result.getText());
        assertEquals(item, result.getItem());
        assertEquals(author, result.getAuthor());
        assertNotNull(result.getCreated());
    }

    @Test
    void fromDto_WithAllParameters_ShouldSetCurrentTimestamp() {
        CommentDto dto = new CommentDto(null, "Test comment", null, null);
        Item item = new Item();
        User author = new User(1L, "Author", "author@email.com");

        LocalDateTime beforeMapping = LocalDateTime.now().minusNanos(1);
        Comment result = mapper.fromDto(dto, item, author);
        LocalDateTime afterMapping = LocalDateTime.now().plusNanos(1);

        assertNotNull(result);
        assertNotNull(result.getCreated());
        assertTrue(result.getCreated().isAfter(beforeMapping) ||
                result.getCreated().equals(beforeMapping));
        assertTrue(result.getCreated().isBefore(afterMapping) ||
                result.getCreated().equals(afterMapping));
    }

    @Test
    void fromDto_WithExistingIdInDto_ShouldIgnoreId() {
        CommentDto dto = new CommentDto(999L, "Test comment", null, null);
        Item item = new Item();
        User author = new User(1L, "Author", "author@email.com");

        Comment result = mapper.fromDto(dto, item, author);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(dto.getText(), result.getText());
    }

    @Test
    void bidirectionalMapping_ShouldWorkCorrectly() {
        User author = new User(1L, "Test Author", "author@test.com");
        Item item = new Item(1L, "Test Item", "Test Description", true, author, null);
        LocalDateTime created = LocalDateTime.of(2023, 12, 1, 10, 30);

        Comment originalComment = new Comment(1L, "Original comment text", item, author, created);

        CommentDto dto = mapper.toDto(originalComment);


        User newAuthor = new User(2L, "New Author", "new@test.com");
        Item newItem = new Item(2L, "New Item", "New Description", true, newAuthor, null);

        Comment mappedComment = mapper.fromDto(dto, newItem, newAuthor);

        assertNotNull(dto);
        assertNotNull(mappedComment);

        assertEquals(originalComment.getText(), dto.getText());
        assertEquals(originalComment.getAuthor().getName(), dto.getAuthorName());

        assertEquals(dto.getText(), mappedComment.getText());
        assertEquals(newItem, mappedComment.getItem());
        assertEquals(newAuthor, mappedComment.getAuthor());
        assertNotNull(mappedComment.getCreated());
    }

    @Test
    void toDto_WithAllFields_ShouldMapAllFieldsCorrectly() {
        User author = new User(1L, "John Doe", "john@email.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, author, null);
        LocalDateTime created = LocalDateTime.of(2023, 12, 15, 14, 30);
        Comment comment = new Comment(1L, "Excellent tool!", item, author, created);

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Excellent tool!", result.getText());
        assertEquals("John Doe", result.getAuthorName());
        assertEquals(created, result.getCreated());
    }

    @Test
    void toDto_WithEmptyCommentText_ShouldMapCorrectly() {
        User author = new User(1L, "Author", "author@email.com");
        Comment comment = new Comment(1L, "", new Item(), author, LocalDateTime.now());

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertEquals("", result.getText());
        assertEquals("Author", result.getAuthorName());
    }

    @Test
    void toDto_WithMinimalData_ShouldMapCorrectly() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test");

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getText());
        assertNull(result.getAuthorName());
        assertNull(result.getCreated());
    }

    @Test
    void fromDto_WithAllDtoFields_ShouldIgnoreIdAndSetTimestamp() {
        CommentDto dto = new CommentDto(999L, "Great item!", "Author Name", LocalDateTime.now().minusDays(1));
        Item item = new Item();
        User author = new User(1L, "Real Author", "real@email.com");

        Comment result = mapper.fromDto(dto, item, author);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Great item!", result.getText());
        assertEquals(item, result.getItem());
        assertEquals(author, result.getAuthor());
        assertNotNull(result.getCreated());
        assertTrue(result.getCreated().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void fromDto_WithDtoContainingCreatedDate_ShouldIgnoreIt() {
        LocalDateTime dtoCreated = LocalDateTime.of(2023, 1, 1, 12, 0);
        CommentDto dto = new CommentDto(null, "Text", null, dtoCreated);
        Item item = new Item();
        User author = new User();

        Comment result = mapper.fromDto(dto, item, author);

        assertNotNull(result);
        assertNotEquals(dtoCreated, result.getCreated());
        assertNotNull(result.getCreated());
    }

    @Test
    void fromDto_WithDtoContainingAuthorName_ShouldIgnoreIt() {
        CommentDto dto = new CommentDto(null, "Text", "Dto Author Name", null);
        Item item = new Item();
        User realAuthor = new User(1L, "Real Author", "real@email.com");

        Comment result = mapper.fromDto(dto, item, realAuthor);

        assertNotNull(result);
        assertEquals(realAuthor, result.getAuthor());
        assertEquals("Real Author", result.getAuthor().getName());
    }

    @Test
    void toDto_WithVeryLongText_ShouldMapCorrectly() {
        String longText = "A".repeat(1000);
        User author = new User(1L, "Author", "author@email.com");
        Comment comment = new Comment(1L, longText, new Item(), author, LocalDateTime.now());

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertEquals(longText, result.getText());
    }

    @Test
    void fromDto_WithVeryLongText_ShouldMapCorrectly() {
        String longText = "B".repeat(1000);
        CommentDto dto = new CommentDto(null, longText, null, null);
        Item item = new Item();
        User author = new User();

        Comment result = mapper.fromDto(dto, item, author);

        assertNotNull(result);
        assertEquals(longText, result.getText());
    }

    @Test
    void fromDto_WithSpecialCharactersInText_ShouldMapCorrectly() {
        String textWithSpecialChars = "Comment with special chars: áéíóú ñ ç @#€£¥";
        CommentDto dto = new CommentDto(null, textWithSpecialChars, null, null);
        Item item = new Item();
        User author = new User();

        Comment result = mapper.fromDto(dto, item, author);

        assertNotNull(result);
        assertEquals(textWithSpecialChars, result.getText());
    }
}

