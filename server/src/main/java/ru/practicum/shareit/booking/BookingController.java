package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingService.addBooking(userId, bookingDtoRequest.getItemId(), bookingDtoRequest);
    }

    @PatchMapping("{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long bookingId,
                             @RequestParam("approved") boolean isApproved) {
        return bookingService.approvedBooking(userId, bookingId, isApproved);
    }

    @GetMapping("{bookingId}")
    public BookingDto findBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long bookingId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findBookingByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        int page = from / size;
        return bookingService.findBookingByBookerId(userId, state, PageRequest.of(page, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingByItemsOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL")
                                                      String state,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        int page = from / size;
        return bookingService.findBookingsByItemsOwnerId(userId, state, PageRequest.of(page, size));
    }
}
