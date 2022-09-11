package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Validated({Create.class}) @RequestBody BookingDtoResponse bookingDtoShort) {
        BookingDto bookingDto = bookingService.addBooking(userId, bookingDtoShort.getItemId(), bookingDtoShort);
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
                                        @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.findBookingByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingByItemsOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL")
                                                      String state) {
        return bookingService.findBookingsByItemsOwnerId(userId, state);
    }
}
