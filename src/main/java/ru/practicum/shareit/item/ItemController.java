package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        ItemDto item = itemService.addItem(userId, itemDto);
        log.info("Была добавлена новая вещь {}, id {}", item.getName(), item.getId());
        return item;
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        itemDto.setId(itemId);
        ItemDto item = itemService.update(userId, itemDto);
        log.info("Была обновлена вещь {}, id {}", item.getName(), item.getId());
        return item;
    }

    @GetMapping("{itemId}")
    public ItemDtoResponse findItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId) {
        return itemService.findItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text,
                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        int page = from / size;
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemService.searchItems(text, PageRequest.of(page, size));
    }

    @GetMapping()
    public List<ItemDtoResponse> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        int page = from / size;
        return itemService.findAll(userId, PageRequest.of(page, size));
    }

    @PostMapping("{itemId}/comment")
    public CommentDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated({Create.class}) @RequestBody CommentDto commentDto,
                          @PathVariable long itemId) {
        commentDto.setCreated(LocalDateTime.now());
        CommentDto comment = itemService.addComment(commentDto, userId, itemId);
        log.info("Был добавлен отзыв для вещи с id {}", itemId);
        return comment;
    }
}
