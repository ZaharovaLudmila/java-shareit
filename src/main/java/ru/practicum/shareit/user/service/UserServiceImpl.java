package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto create(User user) {
        checkUserByEmail(user);
        return UserMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(User user) {
        getUserById(user.getId());
        checkUserByEmail(user);
        return UserMapper.toUserDto(userRepository.update(user));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userRepository.findUserById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!")));
    }

    public void checkUserByEmail(User user) {
        List<User> userList = userRepository.findAll().stream()
                .filter(userInList -> userInList.getEmail().equals(user.getEmail())).collect(Collectors.toList());
        if (userList.size() > 0) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }

    }
}
