package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto);

    ItemDto findItem(long itemId);

    List<ItemDto> findAll(long userId);

    List<ItemDto> searchItems(String text);
}
