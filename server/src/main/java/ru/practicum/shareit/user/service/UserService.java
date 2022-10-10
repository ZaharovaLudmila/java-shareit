package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto create(User user);

    UserDto update(User user);

    void deleteUser(long id);

    UserDto getUserById(long id);
}
