package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository requestRepository;
    Item item1;
    Item item2;
    User user1;
    User user2;

    @BeforeEach
    void beforeEach() {

        user1 = userRepository.save(new User(1, "user1", "user1@mail.ru"));
        user2 = userRepository.save(new User(2, "user2", "user2@mail.ru"));
        item1 = itemRepository.save(
                new Item(1, "item1", "item1 description", true, user1, null));
        item2 = itemRepository.save(
                new Item(2, "item2", "item2 description", true, user2, null));
    }

    @Test
    void findByOwnerId() {
        List<Item> items = itemRepository.findItemsByOwnerIdOrderById(user1.getId(), PageRequest.ofSize(10));
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
        assertEquals(item1.getDescription(), items.get(0).getDescription());
        assertEquals(item1.getOwner(), items.get(0).getOwner());
        assertEquals(item1.getAvailable(), items.get(0).getAvailable());
    }

    @Test
    void searchItems() {
        item1.setDescription("no");
        List<Item> items = itemRepository.searchItems("descrip", PageRequest.ofSize(10));
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item2.getId(), items.get(0).getId());
        assertEquals(item2.getName(), items.get(0).getName());
        assertEquals(item2.getDescription(), items.get(0).getDescription());
        assertEquals(item2.getOwner(), items.get(0).getOwner());
        assertEquals(item2.getAvailable(), items.get(0).getAvailable());
    }

    @Test
    void findByRequestId() {
        ItemRequest itemRequest1 = requestRepository.save(new ItemRequest(1, "item request 1", user2,
                LocalDateTime.now()));
        ItemRequest itemRequest2 = requestRepository.save(new ItemRequest(2, "item request 2", user1,
                LocalDateTime.now()));
        item1.setRequest(itemRequest1);
        item2.setRequest(itemRequest2);
        List<Item> items = itemRepository.findAllByRequestId(itemRequest1.getId());
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
        assertEquals(item1.getDescription(), items.get(0).getDescription());
        assertEquals(item1.getOwner(), items.get(0).getOwner());
        assertEquals(item1.getAvailable(), items.get(0).getAvailable());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }
}