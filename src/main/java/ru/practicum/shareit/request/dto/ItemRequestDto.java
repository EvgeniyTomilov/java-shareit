package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.util.Utils.PATTERN_DATA;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Запрос не возможно создать без описания")
    private String description;
    private Long requesterId;
    @JsonFormat(pattern = PATTERN_DATA)
    private LocalDateTime created;

    private List<ItemDto> items = new ArrayList<>();

    @Override
    public String toString() {
        return "ItemRequestDto{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", requesterId=" + requesterId +
                ", created=" + created +
                '}';
    }
}
