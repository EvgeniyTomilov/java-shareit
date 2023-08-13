package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jsonItemRequestDto;

    @Test
    @SneakyThrows
    void itemRequestDtoTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .requesterId(1L)
                .created(LocalDateTime.now())
                .description("description")
                .build();

        JsonContent<ItemRequestDto> result = jsonItemRequestDto.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.created");
    }

}
