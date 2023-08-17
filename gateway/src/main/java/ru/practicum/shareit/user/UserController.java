package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoGateway;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDtoGateway userDto) {
        log.info("GATEWAY: Create On GateWay: {} - Started", userDto);
        ResponseEntity<Object> user = userClient.create(userDto);
        log.info("GATEWAY: create: {} - Finished", user);
        return user;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Positive @PathVariable("userId") long id,
                                         @RequestBody UserDtoGateway userDto) {
        log.info("GATEWAY: update {} for user id: {}  - Started", userDto, id);
        ResponseEntity<Object> user = userClient.update(userDto, id);
        log.info("GATEWAY: update: {} - Finished", user);
        return user;
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("GATEWAY: findAll - Started");
        ResponseEntity<Object> allUsers = userClient.getUsers();
        return allUsers;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@Positive @PathVariable("userId") long id) {
        log.info("GATEWAY: getUser: {} - Started", id);
        ResponseEntity<Object> user = userClient.getUser(id);
        log.info("GATEWAY: getUser: {} - Finished", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Positive @PathVariable("userId") Integer userId) {
        log.info("GATEWAY: deleteUser: {} userId - Started", userId);
        userClient.delete(userId);
        log.info("GATEWAY: deleteUser: {} userId - Finished", userId);
    }
}
