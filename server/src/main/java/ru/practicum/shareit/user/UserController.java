package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

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
    public UserDto create(@RequestBody UserDto user) {
        return userService.create(UserMapper.toUser(user));
    }

    @PatchMapping("{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto user) {
        user.setId(userId);
        return userService.update(UserMapper.toUser(user));
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto findByID(@PathVariable long userId) {
        return userService.getUserById(userId);
    }
}
