package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping()
    public ItemRequestDtoResponse addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestBody ItemRequestDto requestDto) {
        return requestService.addItemRequest(userId, requestDto);
    }

    @GetMapping()
    public List<ItemRequestDtoResponse> findRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.findAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(name = "from", defaultValue = "0")
                                                Integer from,
                                                @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {
        int page = from / size;
        return requestService.findAll(userId, PageRequest.of(page, size));
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoResponse findRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long requestId) {
        return requestService.findRequest(userId, requestId);
    }
}
