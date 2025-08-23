package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTest {

    private User createUser(Long id) {
        return new User(id, "User " + id, "user" + id + "@example.com");
    }

    private ItemRequest createRequest(Long id) {
        return new ItemRequest();
    }

    @Test
    void testEquals_SameObject() {
        User owner = createUser(1L);
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        assertTrue(item.equals(item));
    }

    @Test
    void testEquals_NullObject() {
        User owner = createUser(1L);
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        assertFalse(item.equals(null));
    }

    @Test
    void testEquals_DifferentClass() {
        User owner = createUser(1L);
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        assertFalse(item.equals("not an item"));
    }

    @Test
    void testEquals_EqualObjects() {
        User owner = createUser(1L);
        Item item1 = new Item(1L, "Item", "Description", true, owner, null);
        Item item2 = new Item(1L, "Item", "Description", true, owner, null);
        assertTrue(item1.equals(item2));
    }

    @Test
    void testEquals_DifferentIds() {
        User owner = createUser(1L);
        Item item1 = new Item(1L, "Item", "Description", true, owner, null);
        Item item2 = new Item(2L, "Item", "Description", true, owner, null);
        assertFalse(item1.equals(item2));
    }

    @Test
    void testEquals_DifferentFieldsSameId() {
        User owner1 = createUser(1L);
        User owner2 = createUser(2L);
        Item item1 = new Item(1L, "Item1", "Description1", true, owner1, null);
        Item item2 = new Item(1L, "Item2", "Description2", false, owner2, createRequest(1L));
        assertTrue(item1.equals(item2));
    }

    @Test
    void testHashCode_EqualObjects() {
        User owner = createUser(1L);
        Item item1 = new Item(1L, "Item", "Description", true, owner, null);
        Item item2 = new Item(1L, "Item", "Description", true, owner, null);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testHashCode_DifferentIds() {
        User owner = createUser(1L);
        Item item1 = new Item(1L, "Item", "Description", true, owner, null);
        Item item2 = new Item(2L, "Item", "Description", true, owner, null);
        assertNotEquals(item1.hashCode(), item2.hashCode());
    }
}
