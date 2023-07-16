package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service("userService")
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.addUser(UserMapper.makeUser(userDto));
        return UserMapper.makeDto(user);
    }

    @Override
    public UserDto getUser(long id) {
        if (userRepository.getUser(id).isEmpty()) {
            log.warn("User {} is not found", id);
            throw new UserNotFoundException("Пользователь id "
                    + id + " не найден");
        }
        User user = userRepository.getUser(id).orElseThrow(() -> new UserNotFoundException("Пользователь id "
                + id + " не найден"));
        return UserMapper.makeDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User user = userRepository.updateUser(UserMapper.makeUser(userDto), id);
        return UserMapper.makeDto(user);
    }

    @Override
    public boolean delete(long id) {
        getUser(id);
        userRepository.deleteUser(id);
        return true;
    }

    @Override
    public void clearAll() {
        userRepository.deleteAllUser();
    }
}
