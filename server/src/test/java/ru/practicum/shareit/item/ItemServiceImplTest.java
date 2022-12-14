package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    ItemService itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository requestRepository;
    User user;
    ItemRequest itemRequest;
    Item item;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        requestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, requestRepository);
        user = new User(1, "userName", "user@email.ru");
        itemRequest = new ItemRequest(1, "request description", user,
                LocalDateTime.of(2022, 9, 14, 13, 44, 22));
        item = new Item(1, "itemName", "item description", true, user, null);
    }

    @Test
    void addItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto itemDto1 = itemService.addItem(1, itemDto);
        assertNotNull(itemDto1);
        assertEquals(ItemDto.class, itemDto1.getClass());
        assertEquals(item.getId(), itemDto1.getId());
        assertEquals(item.getName(), itemDto1.getName());
        assertEquals(item.getDescription(), itemDto1.getDescription());
        assertEquals(item.getAvailable(), itemDto1.getAvailable());
    }

    @Test
    void addItemWithWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemDto itemDto = ItemMapper.toItemDto(item);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addItem(10, itemDto));

        assertEquals("???????????????????????? ?? ?????????? id ???? ????????????!", exception.getMessage());
    }

    @Test
    void addItemWithWrongRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        item.setRequest(itemRequest);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addItem(user.getId(), itemDto));

        assertEquals("???????????? ?? ?????????? id ???? ????????????!", exception.getMessage());
    }

    @Test
    void updateItem() {
        Item itemUpdate = new Item(item.getId(), "updateItem", "updateItemDescription",
                item.getAvailable(), item.getOwner(), item.getRequest());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(itemUpdate)).thenReturn(itemUpdate);
        ItemDto itemDto = ItemMapper.toItemDto(itemUpdate);
        ItemDto itemDto1 = itemService.update(1, itemDto);
        assertNotNull(itemDto1);
        assertEquals(ItemDto.class, itemDto1.getClass());
        assertEquals(itemUpdate.getId(), itemDto1.getId());
        assertEquals(itemUpdate.getName(), itemDto1.getName());
        assertEquals(itemUpdate.getDescription(), itemDto1.getDescription());
        assertEquals(itemUpdate.getAvailable(), itemDto1.getAvailable());
    }

    @Test
    void updateItemWithWrongUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(10, itemDto));

        assertEquals("???????????????????????? ?? ?????????? id ???? ????????????!", exception.getMessage());
    }

    @Test
    void updateItemWithWrongItemId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemDto itemDto = ItemMapper.toItemDto(item);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(10, itemDto));

        assertEquals("???????? ?? ?????????? id ???? ??????????????!", exception.getMessage());
    }

    @Test
    void findItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemsId(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(Collections.emptyList());

        ItemDtoResponse itemDto = ItemMapper.toItemDtoResponse(item,
                null,
                null,
                Collections.emptyList());
        ItemDtoResponse itemDto1 = itemService.findItem(1, 1);
        assertNotNull(itemDto1);
        assertEquals(ItemDtoResponse.class, itemDto1.getClass());
        assertEquals(itemDto.getId(), itemDto1.getId());
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals(itemDto.getDescription(), itemDto1.getDescription());
        assertEquals(itemDto.getAvailable(), itemDto1.getAvailable());
    }

    @Test
    void findItemWithWrongId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.findItem(10, user.getId()));

        assertEquals("???????? ?? ?????????? id ???? ??????????????!", exception.getMessage());
    }

    @Test
    void findAll() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findItemsByOwnerIdOrderById(1, Pageable.ofSize(20)))
                .thenReturn(Collections.singletonList(item));
        when(bookingRepository.findAllByItemsId(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(Collections.emptyList());
        List<ItemDtoResponse> itemsList1 = itemService.findAll(1, Pageable.ofSize(20));
        assertNotNull(itemsList1);
        assertEquals(1, itemsList1.size());
        assertEquals(item.getId(), itemsList1.get(0).getId());
        assertEquals(item.getName(), itemsList1.get(0).getName());
        assertEquals(item.getDescription(), itemsList1.get(0).getDescription());
        assertEquals(item.getAvailable(), itemsList1.get(0).getAvailable());
    }

    @Test
    void searchItems() {
        when(itemRepository.searchItems("text", Pageable.ofSize(20)))
                .thenReturn(Collections.singletonList(item));

        List<ItemDto> items = itemService.searchItems("text", Pageable.ofSize(20));
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(item.getDescription(), items.get(0).getDescription());
        assertEquals(item.getAvailable(), items.get(0).getAvailable());
    }

    @Test
    void addComment() {
        Comment comment = new Comment(1, "text comment", item, user,
                LocalDateTime.of(2022, 9, 16, 13, 44, 44));
        Booking booking = new Booking(1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusSeconds(3),
                item, user, BookingStatus.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAAndItemId(1, 1,
                comment.getCreated()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto commentDto = itemService.addComment(CommentMapper.toCommentDto(comment),
                user.getId(), item.getId());
        assertNotNull(commentDto);
        assertEquals(CommentDto.class, commentDto.getClass());
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(user.getName(), commentDto.getAuthorName());
    }

    @Test
    void findItemGenerateItemDtoResponseTest() {
        Booking lastBooking = new Booking(1, LocalDateTime.of(2022, 9, 14, 13, 22, 22),
                LocalDateTime.of(2022, 9, 15, 13, 22, 22),
                item, user, BookingStatus.APPROVED);

        Booking nextBooking = new Booking(1, LocalDateTime.of(2022, 9, 16, 13, 22, 22),
                LocalDateTime.of(2022, 9, 17, 13, 22, 22),
                item, user, BookingStatus.APPROVED);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemsId(anyLong())).thenReturn(List.of(lastBooking));
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastBookingByItemId(anyLong(), anyLong(), any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findNextBookingByItemId(anyLong(), anyLong(), any()))
                .thenReturn(List.of(nextBooking));
        ItemDtoResponse itemDto = ItemMapper.toItemDtoResponse(item,
                lastBooking,
                nextBooking,
                Collections.emptyList());
        ItemDtoResponse itemDto1 = itemService.findItem(item.getId(), user.getId());
        assertNotNull(itemDto1);
        assertEquals(ItemDtoResponse.class, itemDto1.getClass());
        assertEquals(itemDto.getId(), itemDto1.getId());
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals(itemDto.getDescription(), itemDto1.getDescription());
        assertEquals(itemDto.getAvailable(), itemDto1.getAvailable());
        assertEquals(itemDto.getLastBooking(), itemDto1.getLastBooking());
        assertEquals(itemDto.getNextBooking(), itemDto1.getNextBooking());
    }
}