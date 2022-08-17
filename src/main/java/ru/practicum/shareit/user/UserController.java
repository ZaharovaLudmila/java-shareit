package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PostMapping()
    public UserDto create(@Validated({Create.class}) @RequestBody UserDto user) {
        user = userService.create(UserMapper.toUser(user));
        log.info("Был добавлен новый пользователь {}, id {}, email {}", user.getName(), user.getId(), user.getEmail());
        return user;
    }

    @PatchMapping("{userId}")
    public UserDto update(@PathVariable long userId,
                          @Validated({Update.class}) @RequestBody UserDto user) {
        user.setId(userId);
        user = userService.update(UserMapper.toUser(user));
        log.info("Пользователь {}, id {}, email {} был обновлен", user.getName(), user.getId(), user.getEmail());
        return user;
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        log.info("Пользователь с id {} был удален!", userId);
    }

    @GetMapping("/{userId}")
    public UserDto findByID(@PathVariable long userId) {
        return userService.getUserById(userId);
    }
}
