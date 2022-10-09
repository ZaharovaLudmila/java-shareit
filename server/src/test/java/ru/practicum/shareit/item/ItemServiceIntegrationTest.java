package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=shareIt_test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void findAllByOwnerId() {

        User user = userRepository.save(new User(1, "user Name", "user@email.ru"));
        Item item1 = itemRepository.save(new Item(1, "item name",
                "item description", true, user, null));

        ItemDtoResponse itemDtoResponse1 = ItemMapper.toItemDtoResponse(item1, null,
                null, new ArrayList<>());

        assertThat(itemService.findAll(user.getId(), Pageable.ofSize(10)),
                equalTo(List.of(itemDtoResponse1)));
    }
}
