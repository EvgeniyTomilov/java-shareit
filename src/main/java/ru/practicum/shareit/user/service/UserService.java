package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto getUser(long id);

    Collection<UserDto> getUsers();

    UserDto update(UserDto userDto, long id);

    boolean delete(long id);

    void clearAll();
}
