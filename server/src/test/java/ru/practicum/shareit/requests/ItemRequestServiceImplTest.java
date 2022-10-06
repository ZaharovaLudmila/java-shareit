package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {

    ItemRequestService requestService;
    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    User user;
    ItemRequest itemRequest;
    Item item;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        requestRepository = mock(ItemRequestRepository.class);
        requestService = new ItemRequestServiceImpl(requestRepository, itemRepository, userRepository);
        user = new User(1, "userName", "user@email.ru");
        itemRequest = new ItemRequest(1, "request description", user,
                LocalDateTime.of(2022, 9, 14, 13, 44, 22));
        item = new Item(1, "itemName", "item description", true, user, null);
    }

    @Test
    void addItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDto requestDto = new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated());
        ItemRequestDtoResponse requestDtoResponse = requestService.addItemRequest(user.getId(), requestDto);
        assertNotNull(requestDtoResponse);
        assertEquals(ItemRequestDtoResponse.class, requestDtoResponse.getClass());
        assertEquals(itemRequest.getId(), requestDtoResponse.getId());
        assertEquals(itemRequest.getDescription(), requestDtoResponse.getDescription());
        assertEquals(itemRequest.getCreated(), requestDtoResponse.getCreated());
    }

    @Test
    void addItemRequestWithWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemRequestDto requestDto = new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> requestService.addItemRequest(10, requestDto));

        assertEquals("Пользователь с таким id не найден!", exception.getMessage());
    }

    @Test
    void findAllByOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequestDtoResponse> requestDtoResponses = requestService.findAllByOwner(user.getId());
        assertNotNull(requestDtoResponses);
        assertEquals(1, requestDtoResponses.size());
        assertEquals(ItemRequestDtoResponse.class, requestDtoResponses.get(0).getClass());
        assertEquals(itemRequest.getId(), requestDtoResponses.get(0).getId());
        assertEquals(itemRequest.getDescription(), requestDtoResponses.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), requestDtoResponses.get(0).getCreated());
    }

    @Test
    void findAllByOwnerWithWrongOwnerId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> requestService.findAllByOwner(10));

        assertEquals("Пользователь с таким id не найден!", exception.getMessage());
    }

    @Test
    void findAll() {
        User user2 = new User(2, "userName2", "user2@email.ru");
        ItemRequest itemRequest2 = new ItemRequest(2, "request description2", user2,
                LocalDateTime.of(2022, 9, 14, 13, 44, 22));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByOtherUser(anyLong(), any())).thenReturn(List.of(itemRequest2));
        List<ItemRequestDtoResponse> requestDtoResponses = requestService.findAll(user.getId(),
                Pageable.ofSize(10));
        assertNotNull(requestDtoResponses);
        assertEquals(1, requestDtoResponses.size());
        assertEquals(ItemRequestDtoResponse.class, requestDtoResponses.get(0).getClass());
        assertEquals(itemRequest2.getId(), requestDtoResponses.get(0).getId());
        assertEquals(itemRequest2.getDescription(), requestDtoResponses.get(0).getDescription());
        assertEquals(itemRequest2.getCreated(), requestDtoResponses.get(0).getCreated());
    }

    @Test
    void findRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDtoResponse requestDtoResponse = requestService.findRequest(user.getId(), itemRequest.getId());
        assertNotNull(requestDtoResponse);
        assertEquals(ItemRequestDtoResponse.class, requestDtoResponse.getClass());
        assertEquals(1, requestDtoResponse.getItems().size());
        assertEquals(itemRequest.getId(), requestDtoResponse.getId());
        assertEquals(itemRequest.getDescription(), requestDtoResponse.getDescription());
        assertEquals(itemRequest.getCreated(), requestDtoResponse.getCreated());
    }

    @Test
    void findRequestWithWrongRequestId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> requestService.findRequest(user.getId(), 10));

        assertEquals("Запрос с таким id не найден!", exception.getMessage());
    }
}