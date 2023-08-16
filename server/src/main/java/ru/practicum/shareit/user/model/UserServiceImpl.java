package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service()
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepo;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepo.save(UserMapper.makeUser(userDto)
                .orElseThrow(() -> new UserNotFoundException("User объект не создан")));
        return UserMapper.makeDto(user)
                .orElseThrow(() -> new UserNotFoundException("dto объект не найден"));
    }

    @Override
    public UserDto getUser(long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь id "
                + id + " не найден"));
        return UserMapper.makeDto(user)
                .orElseThrow(() -> new UserNotFoundException("dto объект не найден"));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepo.findAll().stream()
                .map(user -> UserMapper.makeDto(user).get())
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User user = prepareForUpdate(userDto, id);
        return UserMapper.makeDto(userRepo.save(user)).get();
    }

    @Override
    public boolean delete(long id) {
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
