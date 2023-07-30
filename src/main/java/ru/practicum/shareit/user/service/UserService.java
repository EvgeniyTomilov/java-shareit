package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto getUser(long id);

    Collection<UserDto> getUsers();

    UserDto updateUser(UserDto userDto, long id);

    boolean deleteUser(long id);

    void clearAll();
}
