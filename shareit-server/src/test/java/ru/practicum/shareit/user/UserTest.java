package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void testEquals_SameObject() {
        User user = new User(1L, "John", "john@example.com");
        assertTrue(user.equals(user));
    }

    @Test
    void testEquals_NullObject() {
        User user = new User(1L, "John", "john@example.com");
        assertFalse(user.equals(null));
    }

    @Test
    void testEquals_DifferentClass() {
        User user = new User(1L, "John", "john@example.com");
        assertFalse(user.equals("not a user"));
    }

    @Test
    void testEquals_EqualObjects() {
        User user1 = new User(1L, "John", "john@example.com");
        User user2 = new User(1L, "John", "john@example.com");
        assertTrue(user1.equals(user2));
    }

    @Test
    void testEquals_DifferentIds() {
        User user1 = new User(1L, "John", "john@example.com");
        User user2 = new User(2L, "John", "john@example.com");
        assertFalse(user1.equals(user2));
    }

    @Test
    void testHashCode_EqualObjects() {
        User user1 = new User(1L, "John", "john@example.com");
        User user2 = new User(1L, "John", "john@example.com");
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testHashCode_DifferentIds() {
        User user1 = new User(1L, "John", "john@example.com");
        User user2 = new User(2L, "John", "john@example.com");
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }
}
