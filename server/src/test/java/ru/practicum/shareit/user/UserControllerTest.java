package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;

    @SneakyThrows
    @Test
    void create_whenIncomingDtoCorrect_thenReturnStatusIsOkWithBody() {
        UserDto userDtoToCreate = UserDto.builder()
                .name("Name")
                .email("a@a.a")
                .build();

        UserDto afterSave = userDtoToCreate;
        afterSave.setId(1L);
        when(userService.create(userDtoToCreate)).thenReturn(afterSave);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoToCreate), result);
    }

    @SneakyThrows
    @Test
    void create_whenNewUserDtoWithoutEmailAndName_thenStatus400AndThrowException() {
        UserDto userDtoToCreate = new UserDto();
        UserDto afterSave = userDtoToCreate;
        afterSave.setId(1L);
        when(userService.create(userDtoToCreate)).thenReturn(afterSave);

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDtoToCreate)));
    }

    @SneakyThrows
    @Test
    void findAll_whenListUsersIsEmpty_thenReturnEmptyList() {
        List<UserDto> emptyList = List.of(new UserDto());
        when(userService.getUsers()).thenReturn(emptyList);

        mockMvc.perform(get("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(emptyList)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findAll_whenUserExists_thenReturnListWithDto() {
        UserDto userDto = new UserDto();
        userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("a@a.a")
                .build();
        List<UserDto> listWithUser = List.of(userDto);
        when(userService.getUsers()).thenReturn(listWithUser);

        String result = mockMvc.perform(get("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(listWithUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(listWithUser), result);
    }

    @SneakyThrows
    @Test
    void getUser() {
        Long userId = 1L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getUser(userId);
    }

    @SneakyThrows
    @Test
    void update_whenUserWithoutName_thenReturnBadRequest() {
        Long userId = 1L;

        UserDto userDtoToUpdate = new UserDto();
        userDtoToUpdate = UserDto.builder()
                .name("")
                .email("update@update.a")
                .build();

        UserDto updatedUser = UserDto.builder()
                .id(1L)
                .email("update@update.a")
                .name("Name")
                .build();

        when(userService.update(userDtoToUpdate, userId)).thenReturn(updatedUser);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(updatedUser), result);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        Long userId = 1L;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }
}