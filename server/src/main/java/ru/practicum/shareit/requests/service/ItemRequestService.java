package ru.practicum.shareit.requests.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoResponse addItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoResponse> findAllByOwner(long userId);

    List<ItemRequestDtoResponse> findAll(long userId, Pageable pageRequest);

    ItemRequestDtoResponse findRequest(long userId, long requestId);
}
