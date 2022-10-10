package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingStateException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Validated BookItemRequestDto requestDto) {
        log.info("Добавление запроса на бронирование {}", requestDto);
        if (requestDto.getEnd().isBefore(requestDto.getStart())) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты старта!");
        }
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long bookingId,
                                         @RequestParam("approved") boolean isApproved) {
        log.info("Обновление статуса запроса с id {}", bookingId);
        return bookingClient.approvedBooking(userId, bookingId, isApproved);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10")
                                              Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStateException("Unknown state: " + stateParam));
        log.info("Получение запросов {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Получение запроса {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingByItemsOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(value = "state", defaultValue = "ALL")
                                                            String stateParam,
                                                            @PositiveOrZero @RequestParam(name = "from",
                                                                    defaultValue = "0")
                                                            Integer from,
                                                            @Positive @RequestParam(name = "size", defaultValue = "10")
                                                            Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BookingStateException("Unknown state: " + stateParam));
        log.info("Получение запросов по id владельца вещи, userId={}, from={}, size={}", userId, from, size);
        return bookingClient.findBookingsByItemsOwnerId(userId, state, from, size);
    }
}
