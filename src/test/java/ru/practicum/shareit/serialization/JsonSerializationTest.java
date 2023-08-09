package ru.practicum.shareit.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;

@JsonTest
public class JsonSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerialization() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "Sample Item",
                "This is a sample item.",
                true,
                123L,
                456L,
                new ArrayList<>(),
                new BookingForItemDto(),
                new BookingForItemDto()
        );

        String expectedJson = "{\"id\":1,\"name\":\"Sample Item\",\"description\":\"This is a sample item.\",\"available\":true,\"ownerId\":123,\"requestId\":456,\"comments\":[],\"lastBooking\":{},\"nextBooking\":{}}";
        String actualJson = objectMapper.writeValueAsString(itemDto);

        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void testDeserialization() throws Exception {
        String json = "{\"id\":1,\"name\":\"Sample Item\",\"description\":\"This is a sample item.\",\"available\":true,\"ownerId\":123,\"requestId\":456,\"comments\":[],\"lastBooking\":{},\"nextBooking\":{}}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);


    }
}
