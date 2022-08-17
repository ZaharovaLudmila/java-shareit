package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findUserById(long id);

    List<User> findAll();

    User create(User user);

    User update(User user);

    void deleteUser(long id);
}
