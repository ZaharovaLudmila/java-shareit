package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void requestsToDtoTest() {
        User user = new User(1, "userName", "user@email.ru");
        ItemRequest itemRequest = new ItemRequest(1, "request description", user,
                LocalDateTime.of(2022, 9, 14, 13, 44, 22));
        Item item = new Item(1, "itemName", "item description", true, user, null);
        ItemRequestDtoResponse requestDtoResponses = ItemRequestMapper
                .toItemRequestDtoResponse(itemRequest, List.of(item));
        assertNotNull(requestDtoResponses);
        assertEquals(itemRequest.getId(), requestDtoResponses.getId());
        assertEquals(itemRequest.getDescription(), requestDtoResponses.getDescription());
        assertEquals(1, requestDtoResponses.getItems().size());
        assertEquals(itemRequest.getCreated(), requestDtoResponses.getCreated());
    }

}