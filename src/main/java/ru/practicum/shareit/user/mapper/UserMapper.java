package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@UtilityClass
public final class UserMapper {


    public Optional<User> makeUser(UserDto userDto) {
        User user = new User();
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return Optional.of(user);
    }

    public Optional<UserDto> makeDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setId(user.getId());
        return Optional.of(userDto);
    }

    public Optional<User> makeUserWithId(UserDto userDto) {
        User user = new User();
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getId() != 0) {
            user.setId(userDto.getId());
        }
        return Optional.of(user);
    }
}