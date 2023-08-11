package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Utils.SHARER_USER_ID;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private LocalDateTime start;
    private LocalDateTime end;
    private User booker;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        booker = User.builder()
                .email("a@a.a")
                .name("Booker")
                .id(1L)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .start(start)
                .end(end)
                .id(1L)
                .booker(UserMapper.makeDto(booker).orElseThrow())
                .status(StatusOfBooking.WAITING)
                .build();
    }

    @Test
    @SneakyThrows
    void getBooking_whenCorrect_thenReturn200() {
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(SHARER_USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getBookingsOwner_whenCorrect_thenReturn200() {
        mockMvc.perform(get("/bookings/owner")
                        .header(SHARER_USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getBookingsOwner_whenStateNotValid_thenReturn400() {
        mockMvc.perform(get("/bookings/owner")
                        .header(SHARER_USER_ID, 1L)
                        .param("state", "heh"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBookings_whenCorrect_thenReturn200() {
        mockMvc.perform(get("/bookings")
                        .header(SHARER_USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getBookings_whenStateNotValid_thenReturn400() {
        mockMvc.perform(get("/bookings")
                        .header(SHARER_USER_ID, 1L)
                        .param("state", "heh"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBookings_whenFromNotValid_thenReturn400() {
        mockMvc.perform(get("/bookings")
                        .header(SHARER_USER_ID, 1L)
                        .param("from", "-10")
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getBookings_whenSizeNotValid_thenReturn400() {
        mockMvc.perform(get("/bookings")
                        .header(SHARER_USER_ID, 1L)
                        .param("size", "0")
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @SneakyThrows
    void add_whenInputCorrect_thenReturn200WithDto() {
        when(bookingService.addNewBooking(1L, bookingRequestDto)).thenReturn(bookingResponseDto);

        String result = mockMvc.perform(post("/bookings")
                        .header(SHARER_USER_ID, 1L)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    @SneakyThrows
    void add_whenInputWithoutEnd_thenReturn400() {
        bookingRequestDto.setEnd(null);
        mockMvc.perform(post("/bookings")
                        .header(SHARER_USER_ID, 1L)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @SneakyThrows
    void add_whenInputWithoutStart_thenReturn400() {
        bookingRequestDto.setStart(null);
        mockMvc.perform(post("/bookings")
                        .header(SHARER_USER_ID, 1L)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void add_whenInputWithoutItemId_thenReturn400() {
        bookingRequestDto.setItemId(null);
        mockMvc.perform(post("/bookings")
                        .header(SHARER_USER_ID, 1L)
                        .contentType("application/json")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @SneakyThrows
    void approve_whenInputCorrectAndStatusApprove_thenReturn200WithDto() {
        bookingResponseDto.setStatus(StatusOfBooking.APPROVED);
        when(bookingService.approveBooking(1L, 1L, true)).thenReturn(bookingResponseDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(SHARER_USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    @SneakyThrows
    void approve_whenInputCorrectAndStatusRejected_thenReturn200WithDto() {
        bookingResponseDto.setStatus(StatusOfBooking.REJECTED);
        when(bookingService.approveBooking(1L, 1L, false)).thenReturn(bookingResponseDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(SHARER_USER_ID, 1L)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

}