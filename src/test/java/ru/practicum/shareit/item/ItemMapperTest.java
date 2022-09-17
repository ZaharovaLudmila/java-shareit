package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    User user;
    ItemRequest itemRequest;
    Item item;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "userName", "user@email.ru");
        itemRequest = new ItemRequest(1, "request description", user,
                LocalDateTime.of(2022, 9, 14, 13, 44, 22));
        item = new Item(1, "itemName", "item description", true, user, itemRequest);
    }

    @Test
    void itemMapperToDtoTest() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void itemMapperToItemTest() {
        Item item1 = ItemMapper.toItem(ItemMapper.toItemDto(item), user, itemRequest);
        assertNotNull(item1);
        assertEquals(item.getId(), item1.getId());
        assertEquals(item.getName(), item1.getName());
        assertEquals(item.getDescription(), item1.getDescription());
        assertEquals(item.getAvailable(), item1.getAvailable());
        assertEquals(itemRequest.getId(), item1.getRequest().getId());
        assertEquals(user.getId(), item1.getOwner().getId());
    }

    @Test
    void itemMapperToDtoResponseTest() {
        ItemDtoResponse item1 = ItemMapper.toItemDtoResponse(item, null, null, new ArrayList<>());
        assertNotNull(item1);
        assertEquals(item.getId(), item1.getId());
        assertEquals(item.getName(), item1.getName());
        assertEquals(item.getDescription(), item1.getDescription());
        assertEquals(item.getAvailable(), item1.getAvailable());
        assertNull(item1.getLastBooking());
        assertNull(item1.getNextBooking());
        assertEquals(0, item1.getComments().size());
    }
}