package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDtoResponse addItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
        return ItemRequestMapper.toItemRequestDtoResponse(
                requestRepository.save(new ItemRequest(0, itemRequestDto.getDescription(),
                        user, LocalDateTime.now())), new ArrayList<>());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoResponse> findAllByOwner(long userId) {
        checkUser(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return requestsToDto(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoResponse> findAll(long userId, Pageable pageRequest) {
        checkUser(userId);
        List<ItemRequest> requests = requestRepository.findAllByOtherUser(userId, pageRequest);
        return requestsToDto(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDtoResponse findRequest(long userId, long requestId) {
        checkUser(userId);
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с таким id не найден!"));
        return ItemRequestMapper.toItemRequestDtoResponse(request, itemRepository.findAllByRequestId(request.getId()));
    }

    private List<ItemRequestDtoResponse> requestsToDto(List<ItemRequest> requests) {
        return requests.stream().map(request -> ItemRequestMapper.toItemRequestDtoResponse(request,
                itemRepository.findAllByRequestId(request.getId()))).collect(Collectors.toList());
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден!");
        }
    }
}
