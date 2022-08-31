package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @Override
    public UserDto create(User user) {
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto update(User user) {
        UserDto userDto = getUserById(user.getId());
        User oldUser = UserMapper.toUser(userDto);
        if (user.getName() != null && !(user.getName().trim().isBlank())) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !(user.getEmail().trim().isBlank())) {
            oldUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() ->
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
