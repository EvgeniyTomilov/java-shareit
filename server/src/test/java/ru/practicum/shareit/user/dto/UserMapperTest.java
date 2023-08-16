package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    UserDto newUserDto;
    User user;

    @BeforeEach
    void setUp() {
        newUserDto = UserDto.builder()
                .id(null)
                .email("nu@nu.nu")
                .name("New User")
                .build();

        user = User.builder()
                .email("nu@nu.nu")
                .name("New User")
                .id(1L)
                .build();
    }

    @Test
    void makeUser_whenUserDtoCorrect_thenReturnUser() {
        User userFromMapper = UserMapper.makeUser(newUserDto).orElseThrow();
        userFromMapper.setId(1L);
        assertEquals(user, userFromMapper);
    }

    @Test
    void makeUser_whenUserDtoNameNull_thenReturnUser() {
        newUserDto.setName(null);
        user.setName(null);
        User userFromMapper = UserMapper.makeUser(newUserDto).orElseThrow();
        userFromMapper.setId(1L);
        assertEquals(user, userFromMapper);
    }

    @Test
    void makeUser_whenUserDtoEmailNull_thenReturnUser() {
        newUserDto.setEmail(null);
        user.setEmail(null);
        User userFromMapper = UserMapper.makeUser(newUserDto).orElseThrow();
        userFromMapper.setId(1L);
        assertEquals(user, userFromMapper);
    }


    @Test
    void makeDto() {
        UserDto userDtoFromMapper = UserMapper.makeDto(user).get();
        newUserDto.setId(user.getId());
        assertEquals(newUserDto, userDtoFromMapper);
    }

    @Test
    void makeUserWithId() {
        newUserDto.setId(1L);
        User userFromMapper = UserMapper.makeUserWithId(newUserDto).orElseThrow();
        assertEquals(user, userFromMapper);
    }

    @Test
    void makeUserWithId_whenNameNull() {
        newUserDto.setId(1L);
        newUserDto.setName(null);
        user.setName(null);
        User userFromMapper = UserMapper.makeUserWithId(newUserDto).orElseThrow();
        assertEquals(user, userFromMapper);
    }

    @Test
    void makeUserWithId_whenEmailNull() {
        newUserDto.setId(1L);
        newUserDto.setEmail(null);
        user.setEmail(null);
        User userFromMapper = UserMapper.makeUserWithId(newUserDto).orElseThrow();
        assertEquals(user, userFromMapper);
    }

    @Test
    void makeUserWithId_whenId0() {
        newUserDto.setId(0L);
        User userFromMapper = UserMapper.makeUserWithId(newUserDto).orElseThrow();
        assertEquals(user, userFromMapper);
    }

}