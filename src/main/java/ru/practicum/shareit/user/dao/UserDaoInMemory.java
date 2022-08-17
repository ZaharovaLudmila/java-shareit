package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserDaoInMemory implements UserDao {

    private final Map<Long, User> userList = new HashMap<>();
    private long userId = 0;

    @Override
    public Optional<User> findUserById(long id) {
        if (userList.containsKey(id)) {
            return Optional.of(userList.get(id));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userList.values());
    }

    @Override
    public User create(User user) {
        user.setId(++userId);
        userList.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {

        User oldUser = userList.get(user.getId());
        if (user.getName() != null && !(user.getName().trim().isBlank())) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !(user.getEmail().trim().isBlank())) {
            oldUser.setEmail(user.getEmail());
        }
        userList.put(user.getId(), oldUser);

        return oldUser;
    }

    @Override
    public void deleteUser(long id) {
        userList.remove(id);

    }
}
