package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    User user;
    User user2;
    Item item;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "userName", "user@email.ru");
        user2 = new User(2, "userName2", "user2@email.ru");
        item = new Item(1, "itemName", "item description", true, user2, null);
        booking = new Booking(1, LocalDateTime.of(2022, 9, 16, 13, 22, 22),
                LocalDateTime.of(2022, 9, 17, 13, 22, 22), item, user,
                BookingStatus.WAITING);
    }

    @Test
    void bookingMapperToBookingDtoShortTest() {
        BookingDtoShort bookingDto = BookingMapper.toBookingDtoShort(booking);
        assertNotNull(bookingDto);
        assertEquals(BookingDtoShort.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
    }

    @Test
    void bookingMapperToBookingTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        Booking newBooking = BookingMapper.toBooking(bookingDto, item, user);
        assertNotNull(newBooking);
        assertEquals(Booking.class, newBooking.getClass());
        assertEquals(booking.getId(), newBooking.getId());
        assertEquals(booking.getBooker(), newBooking.getBooker());
        assertEquals(booking.getItem(), newBooking.getItem());
        assertEquals(booking.getStatus(), newBooking.getStatus());
        assertEquals(booking.getStart(), newBooking.getStart());
        assertEquals(booking.getEnd(), newBooking.getEnd());
    }

}