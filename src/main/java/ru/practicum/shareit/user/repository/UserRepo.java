package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo {
    User save(User user);

    User update(User user, long id);

    List<UserDto> findAll();

    Optional<User> findUser(long id);

    boolean delete(long id);

    void deleteAllUser();
}