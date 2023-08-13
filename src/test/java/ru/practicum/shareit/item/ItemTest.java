package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {

    @Test
    void shouldSetId() {
        Item testItem = new Item();
        testItem.setId(1L);
        assertEquals(1, testItem.getId(), "id не установлен, либо не получен");
    }
}