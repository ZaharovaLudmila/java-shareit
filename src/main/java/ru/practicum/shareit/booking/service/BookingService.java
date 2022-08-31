package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long userId, long itemId, BookingDtoResponse bookingDtoShort);

    BookingDto approvedBooking(long userId, long bookingId, boolean isApproved);

    BookingDto findBooking(long userId, long bookingId);

    List<BookingDto> findBookingByBookerId(long userId, String state);

    List<BookingDto> findBookingsByItemsOwnerId(long userId, String state);
}
