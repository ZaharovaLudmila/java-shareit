package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public List<UserDto> findAll() {
        return userDao.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto create(User user) {
        checkUserByEmail(user);
        return UserMapper.toUserDto(userDao.create(user));
    }

    @Override
    public UserDto update(User user) {
        getUserById(user.getId());
        checkUserByEmail(user);
        return UserMapper.toUserDto(userDao.update(user));
    }

    @Override
    public void deleteUser(long id) {
        userDao.deleteUser(id);
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userDao.findUserById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!")));
    }

    public void checkUserByEmail(User user) {
        List<User> userList = userDao.findAll().stream()
                .filter(userInList -> userInList.getEmail().equals(user.getEmail())).collect(Collectors.toList());
        if (userList.size() > 0) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }

    }
}
