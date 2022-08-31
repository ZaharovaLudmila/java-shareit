package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("checkstyle:Regexp")
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user)));
    }

    @Transactional
    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        checkUser(userId);
        Item oldItem = itemRepository.findById(itemDto.getId()).orElseThrow(() ->
                new NotFoundException("Вещь с таким id не найдена!"));
        if (oldItem != null) {
            if (oldItem.getOwner().getId() == userId) {
                if (itemDto.getName() != null && !(itemDto.getName().trim().isBlank())) {
                    oldItem.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null && !(itemDto.getDescription().trim().isBlank())) {
                    oldItem.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    oldItem.setAvailable(itemDto.getAvailable());
                }
                return ItemMapper.toItemDto(itemRepository.save(oldItem));
            } else {
                throw new NotFoundException("Пользователь с id " + userId + " не является владельцем данной вещи!");
            }
        } else {
            throw new NotFoundException("Вещь не найдена!");
        }

    }

    @Override
    public ItemDtoResponse findItem(long itemId, long userId) {
        checkUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с таким id не найдена!"));
        return generateItemDtoResponse(item, userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoResponse> findAll(long userId) {
        checkUser(userId);
        List<Item> itemList = itemRepository.findItemsByOwnerId(userId);
        List<ItemDtoResponse> itemDtoResponseList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoResponseList.add(generateItemDtoResponse(item, userId));
        }
        return itemDtoResponseList;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с таким id не найдена!"));
        LocalDateTime now = LocalDateTime.now();
        Booking booking = bookingRepository.findAllByBookerIdAAndItemId(userId, itemId, now).stream()
                .findFirst().orElseThrow(() -> new BookingStateException("Запрос на бронирование не найден!"));
        if (booking != null) {
            return CommentMapper.toCommentDto(commentRepository.save(new Comment(commentDto.getId(),
                    commentDto.getText(), item, user, LocalDateTime.now())));
        } else {
            throw new RuntimeException("Пользователь с id " + " не брал вещь с id" + " в аренду!");
        }
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден!");
        }
    }

    private ItemDtoResponse generateItemDtoResponse(Item item, long userId) {
        List<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking;
        Booking nextBooking;
        bookingList = bookingRepository.findAllByItemsId(item.getId());
        List<CommentDto> comments = commentRepository.findCommentsByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (bookingList.isEmpty()) {
            return new ItemDtoResponse(item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    null,
                    null,
                    comments);
        } else {
            lastBooking = bookingRepository.findLastBookingByItemId(item.getId(), userId, now)
                    .stream().findFirst().orElse(null);
            nextBooking = bookingRepository.findNextBookingByItemId(item.getId(), userId, now)
                    .stream().findFirst().orElse(null);
            return ItemMapper.toItemDtoResponse(item, lastBooking, nextBooking, comments);
        }
    }
}
