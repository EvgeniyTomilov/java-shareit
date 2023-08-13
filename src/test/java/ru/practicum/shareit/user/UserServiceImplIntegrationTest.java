package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserServiceImplIntegrationTest {
    private UserDto userDto;

    private final UserService userService;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .email("nu@nu.nu")
                .name("New User")
                .build();
    }

    @Test
    void whenAddUser() {
        UserDto expected = userService.addUser(userDto);

        UserDto actual = userService.getUser(expected.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenGetUsers() {
        UserDto userDtoForSave = UserDto.builder()
                .email("email@email.com")
                .name("name")
                .build();
        UserDto userDto1 = userService.addUser(userDto);
        UserDto userDto2 = userService.addUser(userDtoForSave);
        List<UserDto> expected = List.of(userDto1, userDto2);

        Collection<UserDto> actual = userService.getUsers();

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenUpdateUser() {
        UserDto userDtoForUpdate = UserDto.builder()
                .email("email@email.com")
                .name("name")
                .build();
        UserDto oldUser = userService.addUser(userDto);

        UserDto actual = userService.updateUser(userDtoForUpdate, oldUser.getId());
        userDtoForUpdate.setId(oldUser.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(userDtoForUpdate);
    }

    @Test
    void whenDeleteUser() {
        UserDto savedUser = userService.addUser(userDto);

        userService.deleteUser(savedUser.getId());
        Collection<UserDto> actual = userService.getUsers();

        assertThat(actual).isEmpty();
    }

    @Test
    void whenClearAll() {
        UserDto userDtoForSave = UserDto.builder()
                .email("email@email.com")
                .name("name")
                .build();
        userService.addUser(userDto);
        userService.addUser(userDtoForSave);

        userService.clearAll();
        Collection<UserDto> actual = userService.getUsers();

        assertThat(actual).isEmpty();
    }

}