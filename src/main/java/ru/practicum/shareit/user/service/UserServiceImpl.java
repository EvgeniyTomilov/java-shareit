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
import java.util.stream.Collectors;

@Service()
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userRepo.save(UserMapper.makeUser(userDto)
                .orElseThrow(() -> new NullPointerException("User объект не создан")));
        return UserMapper.makeDto(user)
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    @Override
    public UserDto getUser(long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь id "
                + id + " не найден"));
        return UserMapper.makeDto(user)
                .orElseThrow(() -> new NullPointerException("dto объект не найден"));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepo.findAll().stream()
                .map(user -> UserMapper.makeDto(user).get())
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User user = prepareForUpdate(userDto, id);
        return UserMapper.makeDto(userRepo.save(user)).get();
    }

    @Override
    public boolean deleteUser(long id) {
        getUser(id);
        userRepo.deleteById(id);
        return true;
    }

    @Override
    public void clearAll() {
        userRepo.deleteAll();
    }

    private User prepareForUpdate(UserDto userDto, long id) {
        UserDto userStorage = getUser(id);
        if (userDto.getName() != null) {
            userStorage.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userStorage.setEmail(userDto.getEmail());
        }

        return UserMapper.makeUserWithId(userStorage).get();
    }
}
