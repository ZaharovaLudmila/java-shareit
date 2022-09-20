package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestDtoResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList())
        );
    }
}
