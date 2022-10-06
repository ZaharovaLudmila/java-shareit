package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        itemDto.setId(itemId);
        return itemService.update(userId, itemDto);
    }

    @GetMapping("{itemId}")
    public ItemDtoResponse findItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId) {
        return itemService.findItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text,
                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        int page = from / size;
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemService.searchItems(text, PageRequest.of(page, size));
    }

    @GetMapping()
    public List<ItemDtoResponse> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        int page = from / size;
        return itemService.findAll(userId, PageRequest.of(page, size));
    }

    @PostMapping("{itemId}/comment")
    public CommentDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody CommentDto commentDto,
                             @PathVariable long itemId) {
        commentDto.setCreated(LocalDateTime.now());
        return itemService.addComment(commentDto, userId, itemId);
    }
}
