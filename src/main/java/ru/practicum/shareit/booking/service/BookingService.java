package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long userId, long itemId, BookingDtoRequest bookingDtoShort);

    BookingDto approvedBooking(long userId, long bookingId, boolean isApproved);

    BookingDto findBooking(long userId, long bookingId);

    List<BookingDto> findBookingByBookerId(long userId, String state, Pageable pageRequest);

    List<BookingDto> findBookingsByItemsOwnerId(long userId, String state, Pageable pageRequest);
}
