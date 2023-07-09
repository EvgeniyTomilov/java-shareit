package ru.practicum.shareit.user.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository("UserRepository")
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idGenerator = 0;

    @Override
    public User addUser(User user) {
        isEmailFree(user.getEmail());
        user.setId(++idGenerator);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user, long id) {
        User userStorage = getUser(id).orElseThrow(() -> {
            log.error("User Id {} is not found. Update error", id);
            throw new UserNotFoundException("User is not found");
        });
        if (user.getName() != null) {
            userStorage.setName(user.getName());
        }
        if (user.getEmail() != null && user.getEmail().equals(userStorage.getEmail())
                || user.getEmail() != null && isEmailFree(user.getEmail())) {
            userStorage.setEmail(user.getEmail());
        }

        users.put(id, userStorage);
        return userStorage;
    }

    private boolean isEmailFree(String email) {
        if (users.values().stream()
                .filter(user1 -> user1.getEmail().equals(email))
                .collect(Collectors.toList()).size() != 0) {
            log.warn("email {} already exists", email);
            throw new ConflictException("User with " + email + " already exists");
        }
        return true;
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Количество пользователей составляет: " + users.size());
        return users.values().stream()
                .map(user -> UserMapper.makeDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public void deleteAllUser() {
        if (!users.isEmpty()) {
            users.clear();
        }
    }
}
