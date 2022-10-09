package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=shareIt_test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    @Test
    void updateUser() {
        User user = userRepository.save(new User(1, "user Name", "user@email.ru"));

        User updateUser = new User(user.getId(), "user updateName", "user@email.ru");
        assertThat(userService.update(updateUser), equalTo(UserMapper.toUserDto(updateUser)));
    }
}
