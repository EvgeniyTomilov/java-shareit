package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("SERVER: Create for: {} - Started", userDto);
        UserDto userDto1 = userService.create(userDto);
        log.info("SERVER: create: {} - Server finished", userDto1);
        return userDto1;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") long id,
                          @RequestBody UserDto userDto) {
        log.info("SERVER: update {} for user id: {}  - Started", userDto, id);
        UserDto user = userService.update(userDto, id);
        log.info("SERVER: update: {} - Finished", user);
        return user;
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("SERVER: findAll on Server - Started");
        List<UserDto> allUsers = userService.getUsers();
        log.info("SERVER: findAll: найдено {} пользователей - Finished", allUsers.size());
        return allUsers;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") long id) {
        log.info("SERVER: getUser: {} - Started", id);
        UserDto user = userService.getUser(id);
        log.info("SERVER: getUser: {} - Finished", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Integer userId) {
        log.info("SERVER: deleteUser: {} userId - Started", userId);
        boolean isDel = userService.delete(userId);
        log.info("SERVER: deleteUser: {} userId - Finished {} ", userId, isDel);
    }
}