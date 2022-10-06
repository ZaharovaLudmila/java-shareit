package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void userMapperTest() {
        User user = new User(1, "userName", "user@email.ru");
        UserDto userDto = UserMapper.toUserDto(user);
        assertNotNull(userDto);
        assertEquals(UserDto.class, userDto.getClass());
        assertEquals(userDto.getId(), userDto.getId());
        assertEquals(userDto.getName(), userDto.getName());

        User user1 = UserMapper.toUser(userDto);
        assertNotNull(user1);
        assertEquals(User.class, user1.getClass());
        assertEquals(user1.getId(), user.getId());
        assertEquals(user1.getName(), user.getName());
    }

}