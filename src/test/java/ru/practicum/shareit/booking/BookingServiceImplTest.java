package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    BookingService bookingService;
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    BookingRepository bookingRepository;

    User user;
    User user2;
    Item item;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        user = new User(1, "userName", "user@email.ru");
        user2 = new User(2, "userName2", "user2@email.ru");
        item = new Item(1, "itemName", "item description", true, user2, null);
        booking = new Booking(1, LocalDateTime.of(2022, 9, 16, 13, 22, 22),
                LocalDateTime.of(2022, 9, 17, 13, 22, 22), item, user,
                BookingStatus.WAITING);
    }

    @Test
    void addBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(booking.getStart(),
                booking.getEnd(), item.getId());
        BookingDto bookingDto = bookingService.addBooking(user.getId(), item.getId(), bookingDtoRequest);
        assertNotNull(bookingDto);
        assertEquals(BookingDto.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void addBookingWhenEndIsBeforeStart() {

        booking.setEnd(booking.getStart().minusDays(10));
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(booking.getStart(),
                booking.getEnd(), item.getId());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.addBooking(user.getId(), item.getId(), bookingDtoRequest));

        assertEquals("Дата окончания не может быть раньше даты старта!", exception.getMessage());
    }

    @Test
    void approvedBooking() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.approvedBooking(user2.getId(), booking.getId(), true);
        assertNotNull(bookingDto);
        assertEquals(BookingDto.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
    }

    @Test
    void findBooking() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.findBooking(user.getId(), booking.getId());
        assertNotNull(bookingDto);
        assertEquals(BookingDto.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void findBookingWithWrongId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(user.getId(), 100));

        assertEquals("Запрос на бронирование с таким id не найден!", exception.getMessage());
    }

    @Test
    void findBookingWithUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.findBooking(10, booking.getId()));

        assertEquals("Поиск запроса на бронирование по id возможен только для автора запроса " +
                " или для владельца вещи!", exception.getMessage());
    }

    @Test
    void findBookingByBookerId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findBookingByBookerId(user.getId(), "ALL",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByBookerId(user.getId(), "CURRENT",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByBookerId(user.getId(), "PAST",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByBookerId(user.getId(), "FUTURE",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByBookerId(user.getId(), "WAITING",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByBookerId(user.getId(), "REJECTED",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        BookingStateException exception = assertThrows(BookingStateException.class, () ->
                bookingService.findBookingByBookerId(user.getId(), "UNKNOWN", Pageable.ofSize(10)));
        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void findBookingsByItemsOwnerId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemsOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllCurrentByItemsOwnerId(anyLong(),
                any(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllPastByItemsOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllFutureByItemsOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllStatusByItemsOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findBookingsByItemsOwnerId(user2.getId(), "ALL",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingsByItemsOwnerId(user2.getId(), "CURRENT",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingsByItemsOwnerId(user2.getId(), "PAST",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingsByItemsOwnerId(user2.getId(), "FUTURE",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingsByItemsOwnerId(user2.getId(), "WAITING",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingsByItemsOwnerId(user2.getId(), "REJECTED",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        BookingStateException exception = assertThrows(BookingStateException.class, () ->
                bookingService.findBookingByBookerId(user2.getId(), "UNKNOWN", Pageable.ofSize(10)));
        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }
}