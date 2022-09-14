package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Validated({Create.class}) @RequestBody BookingDtoRequest bookingDtoRequest) {
        BookingDto bookingDto = bookingService.addBooking(userId, bookingDtoRequest.getItemId(), bookingDtoRequest);
        log.info("Был добавлен новый запрос на бронирование с id {}", bookingDto.getId());
        return bookingDto;
    }

    @PatchMapping("{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long bookingId,
                             @RequestParam("approved") boolean isApproved) {
        BookingDto bookingDto = bookingService.approvedBooking(userId, bookingId, isApproved);
        log.info("Был обновлен статус запроса с id {}", bookingDto.getId());
        return bookingDto;
    }

    @GetMapping("{bookingId}")
    public BookingDto findBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long bookingId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findBookingByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                  Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10")
                                                  Integer size) {
        int page = from / size;
        return bookingService.findBookingByBookerId(userId, state, PageRequest.of(page, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingByItemsOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL")
                                                      String state,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                      Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10")
                                                      Integer size) {
        int page = from / size;
        return bookingService.findBookingsByItemsOwnerId(userId, state, PageRequest.of(page, size));
    }
}
