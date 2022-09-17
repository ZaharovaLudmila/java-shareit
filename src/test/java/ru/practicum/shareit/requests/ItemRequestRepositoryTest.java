package ru.practicum.shareit.requests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository requestRepository;
    User user1;
    ItemRequest request;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1, "user1", "user1@mail.ru"));
        request = requestRepository.save(new ItemRequest(1, "description request",
                user1, LocalDateTime.now()));
    }

    @Test
    void findAllByRequesterIdTest() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(user1.getId());
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
        assertEquals(request.getDescription(), requests.get(0).getDescription());
        assertEquals(user1.getName(), requests.get(0).getRequester().getName());
    }

    @Test
    void findAllByOtherUserTest() {
        User user2 = userRepository.save(new User(2, "user2", "user2@mail.ru"));
        List<ItemRequest> requests = requestRepository.findAllByOtherUser(user2.getId(), Pageable.ofSize(10));
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
        assertEquals(request.getDescription(), requests.get(0).getDescription());
        assertNotEquals(user2.getName(), requests.get(0).getRequester().getName());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }

}