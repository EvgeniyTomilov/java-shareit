package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("create: {} - Started", userDto);
        UserDto user = userService.addUser(userDto);
        log.info("create: {} - Finished", user);
        return user;
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("findAll - Started");
        List<UserDto> allUsers = userService.getUsers();
        log.info("findAll: найдено {} пользователей - Finished", allUsers.size());
        return allUsers;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") long id) {
        log.info("getUser: {} - Started", id);
        UserDto user = userService.getUser(id);
        log.info("getUser: {} - Finished", user);
        return user;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") long id,
                              @RequestBody UserDto userDto) {
        log.info("update {} for user id: {}  - Started", userDto, id);
        UserDto user = userService.updateUser(userDto, id);
        log.info("update: {} - Finished", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Integer userId) {
        log.info("deleteUser: {} userId - Started", userId);
        boolean isDel = userService.deleteUser(userId);
        log.info("deleteUser: {} userId - Finished {} ", userId, isDel);
    }
}