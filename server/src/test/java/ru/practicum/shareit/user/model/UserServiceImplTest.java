package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserDto newUserDto;
    private User userFromRepo;

    @Mock
    private UserRepository userRepo;
    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @BeforeEach
    void setUp() {
        newUserDto = UserDto.builder()
                .email("nu@nu.nu")
                .name("New User")
                .build();

        userFromRepo = User.builder()
                .email("nu@nu.nu")
                .name("New User")
                .id(1L)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void create_whenUserDtoCorrect_thenReturnDto() {
        User newUser = UserMapper.makeUser(newUserDto).orElseThrow();
        when(userRepo.save(newUser)).thenReturn(userFromRepo);
        UserDto expectedDto = UserMapper.makeDto(userFromRepo).orElseThrow();
        assertEquals(expectedDto, userService.create(newUserDto));
    }

    @Test
    void create_whenEmailIncorrect_thenThrowException() {
        UserDto withIncorrectEmail = UserDto.builder()
                .email("nu@nu")
                .name("New User")
                .build();
        when(userRepo.save(UserMapper.makeUser(withIncorrectEmail).orElseThrow())).thenThrow(ValidationException.class);
        assertThrows(ValidationException.class, () -> userService.create(withIncorrectEmail));
    }

    @Test
    void create_withoutName_thenThrowException() {
        UserDto withIncorrectEmail = UserDto.builder()
                .email("nu@nu.nu")
                .name("")
                .build();
        when(userRepo.save(UserMapper.makeUser(withIncorrectEmail).orElseThrow())).thenThrow(ValidationException.class);
        assertThrows(ValidationException.class, () -> userService.create(withIncorrectEmail));

    }

    @Test
    void getUser_whenIdIsCorrect_thenReturnDto() {
        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(userFromRepo));
        UserDto expectedDto = UserMapper.makeDto(userFromRepo).orElseThrow();
        assertEquals(expectedDto, userService.getUser(1L));
    }

    @Test
    void getUser_whenIdIsNotCorrect_thenThrowException() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.getUser(999L));
        UserNotFoundException ex2 = assertThrows(UserNotFoundException.class, () -> userService.getUser(-999L));
        ex.getMessage();
        ex2.getMessage();
    }

    @Test
    void getUsers_whenUserFound_returnListWithUserDto() {
        UserDto fromRepo = newUserDto;
        fromRepo.setId(1L);
        List<User> expectedList = List.of(userFromRepo);
        List<UserDto> expectedListDto = List.of(fromRepo);
        when(userRepo.findAll()).thenReturn(expectedList);
        assertEquals(expectedListDto, userService.getUsers());
    }

    @Test
    void getUsers_whenUserNotFound_returnEmptyList() {
        List<User> expectedList = List.of(new User());
        List<UserDto> expectedListDto = List.of(new UserDto());
        when(userRepo.findAll()).thenReturn(expectedList);
        assertEquals(expectedListDto, userService.getUsers());
    }

    @Test
    void update_whenCorrect_thenReturnUpdateDto() {
        UserDto dtoUpdate = UserDto.builder()
                .name("Update Name")
                .email("u@u.u")
                .id(1L)
                .build();

        User userUpdate = User.builder()
                .name("Update Name")
                .email("u@u.u")
                .id(1L)
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(userFromRepo));
        when(userRepo.save(userUpdate)).thenReturn(userUpdate);
        assertEquals(dtoUpdate, userService.update(dtoUpdate, 1L));

        verify(userRepo).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("Update Name", savedUser.getName());
        assertEquals("u@u.u", savedUser.getEmail());
        assertEquals(1L, savedUser.getId());

    }

    @Test
    void update_whenUpdateOnlyEmail_thenReturnUpdateDto() {
        UserDto dtoUpdate = UserDto.builder()
                .email("u@u.u")
                .id(1L)
                .build();

        User userUpdate = User.builder()
                .name("New User")
                .email("u@u.u")
                .id(1L)
                .build();

        UserDto expectedDto = dtoUpdate;
        expectedDto.setName("New User");

        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(userFromRepo));
        when(userRepo.save(userUpdate)).thenReturn(userUpdate);
        assertEquals(dtoUpdate, userService.update(dtoUpdate, 1L));
    }

    @Test
    void delete() {
        when(userRepo.findById(1L)).thenReturn(Optional.ofNullable(userFromRepo));
        userService.delete(1L);
        verify(userRepo).deleteById(1L);
    }

    @Test
    void clearAll() {
        userService.clearAll();
        verify(userRepo).deleteAll();
    }
}