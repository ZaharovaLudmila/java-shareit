package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    UserService userService;
    @Mock
    UserRepository userRepository;
    User user;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = new User(1, "userName", "user@email.ru");
    }

    @Test
    void findAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> list = userService.findAll();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(user.getId(), list.get(0).getId());
        assertEquals(user.getName(), list.get(0).getName());
        assertEquals(user.getEmail(), list.get(0).getEmail());
    }

    @Test
    void createUser() {
        when(userRepository.save(any())).thenReturn(user);
        UserDto userDto = userService.create(user);
        assertNotNull(userDto);
        assertEquals(UserDto.class, userDto.getClass());
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void updateUser() {
        User updateUser = new User(user.getId(), "update user", "updateEmail@mail.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(updateUser);
        UserDto userDto = userService.update(user);
        assertNotNull(userDto);
        assertEquals(UserDto.class, userDto.getClass());
        assertEquals(updateUser.getId(), userDto.getId());
        assertEquals(updateUser.getName(), userDto.getName());
        assertEquals(updateUser.getEmail(), userDto.getEmail());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto userDto = userService.getUserById(user.getId());
        assertNotNull(userDto);
        assertEquals(UserDto.class, userDto.getClass());
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void getUserByIdWrongId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(5));

        assertEquals("Пользователь с таким id не найден!", exception.getMessage());
    }
}