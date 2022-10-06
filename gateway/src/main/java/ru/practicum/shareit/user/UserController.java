package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @PostMapping()
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody UserDto user) {
        log.info("Добавление нового пользователя {}", user);
        return userClient.create(user);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId,
                                         @Validated({Update.class}) @RequestBody UserDto user) {
        log.info("Обновление пользователя {}, id {}", user, userId);
        return userClient.update(userId, user);
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Удаление пользователя с id {}", userId);
        userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findByID(@PathVariable long userId) {
        return userClient.getUserById(userId);
    }
}
