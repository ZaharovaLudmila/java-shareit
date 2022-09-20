package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

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
@DirtiesContext
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;

    @Test
    void findAllByOwnerId() {

        User user = new User(1, "user Name", "user@email.ru");
        Item item1 = new Item(1, "item name", "item description", true, user, null);
        Item item2 = new Item(2, "item2 name", "item2 description", true, user, null);

        em.createNativeQuery("insert into users (user_name, email) values (?, ?)")
                .setParameter(1, user.getName())
                .setParameter(2, user.getEmail())
                .executeUpdate();

        em.createNativeQuery("insert into items (item_name, description, is_available, owner_id) values (?, ?, ?,?)")
                .setParameter(1, item1.getName())
                .setParameter(2, item1.getDescription())
                .setParameter(3, item1.getAvailable())
                .setParameter(4, user.getId())
                .executeUpdate();

        em.createNativeQuery("insert into items (item_name, description, is_available, owner_id) values (?, ?, ?,?)")
                .setParameter(1, item2.getName())
                .setParameter(2, item2.getDescription())
                .setParameter(3, item2.getAvailable())
                .setParameter(4, user.getId())
                .executeUpdate();

        ItemDtoResponse itemDtoResponse1 = ItemMapper.toItemDtoResponse(item1, null,
                null, new ArrayList<>());
        ItemDtoResponse itemDtoResponse2 = ItemMapper.toItemDtoResponse(item2, null,
                null, new ArrayList<>());
        assertThat(itemService.findAll(user.getId(), Pageable.ofSize(10)),
                equalTo(List.of(itemDtoResponse1, itemDtoResponse2)));
    }
}