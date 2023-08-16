package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerMockitoTest {
    private LocalDateTime time;
    private CommentRequestDto commentDtoToAdd;
    private CommentDto afterSave;
    private User author;
    private ItemRequest itemRequest;
    private User owner;
    private Item item;
    private Comment commentToSave;
    private Comment commentFromRepo;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        time = LocalDateTime.now();

        commentDtoToAdd = CommentRequestDto.builder()
                .text("noComment")
                .build();


        afterSave = CommentDto.builder()
                .authorName("AuthorOfComment")
                .text("noComment")
                .id(1L)
                .created(time)
                .build();

        author = User.builder()
                .id(1L)
                .name("AuthorOfComment")
                .email("a@a.a")
                .build();

        itemRequest = ItemRequest.builder()
                .requester(author)
                .description("qwer")
                .created(time)
                .id(1L)
                .build();

        owner = User.builder()
                .id(2L)
                .name("OwnerOfItem")
                .email("o@o.o")
                .build();

        item = Item.builder()
                .owner(owner)
                .isAvailable(true)
                .description("desc")
                .name("Item")
                .request(itemRequest)
                .build();

        commentToSave = Comment.builder()
                .author(author)
                .item(item)
                .created(time)
                .text("noComment")
                .build();

        commentFromRepo = CommentMapper.requestToEntity(item, author, commentDtoToAdd.getText());
        commentFromRepo.setId(1L);
    }

    @Test
    @SneakyThrows
    void addComment_whenCorrect_thenStatus200AndReturnCommentDto() {
        when(itemService.addNewCommentToItem(commentDtoToAdd)).thenReturn(afterSave);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentDtoToAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @SneakyThrows
    void addComment_whenInputNotValid_thenStatus400() {
        commentDtoToAdd.setText("");
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                .header("X-Sharer-User-Id", 1L)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(commentDtoToAdd)));
    }

    @Test
    @SneakyThrows
    void getItems_whenCorrect_thenStatus200() {
        mockMvc.perform(get("/items", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void searchItems_whenCorrect_thenStatus200() {
        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void deleteItem_whenCorrect_thenStatus200() {
        mockMvc.perform(delete("/items/{itemId}", 1L)
                        .param("text", "text")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}

