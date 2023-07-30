package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user, long id);

    List<UserDto> getUsers();

    Optional<User> getUser(long id);

    void deleteUser(long id);

    void deleteAllUser();
}
