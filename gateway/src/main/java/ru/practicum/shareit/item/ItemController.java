package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Добавление новой вещи {}", itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Validated({Update.class}) @RequestBody ItemDto itemDto,
                                         @PathVariable long itemId) {
        itemDto.setId(itemId);
        log.info("Обновление вещи {}, id {}", itemDto.getName(), itemId);
        return itemClient.update(userId, itemDto);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> findItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long itemId) {
        return itemClient.findItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                              Integer size) {

        return itemClient.searchItems(text, from, size);
    }

    @GetMapping()
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.findAll(userId, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> updateComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Validated({Create.class}) @RequestBody CommentDto commentDto,
                                                @PathVariable long itemId) {
        commentDto.setCreated(LocalDateTime.now());
        log.info("Добавление отзыва для вещи с id {}", itemId);
        return itemClient.addComment(commentDto, userId, itemId);
    }
}
