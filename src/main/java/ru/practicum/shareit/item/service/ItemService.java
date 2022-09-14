package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto);

    ItemDtoResponse findItem(long itemId, long userId);

    List<ItemDtoResponse> findAll(long userId, Pageable pageRequest);

    List<ItemDto> searchItems(String text, Pageable pageRequest);

    CommentDto addComment(CommentDto commentDto, long userId, long itemId);
}
