package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public BookingDto addBooking(long userId, long itemId, BookingDtoRequest bookingDtoRequest) {
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты старта!");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с таким id не найдена!"));
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Владелец вещи не может добавить запрос на бронирование своей же вещи!");
        }
        if (item.getAvailable()) {
            Booking booking = new Booking(0, bookingDtoRequest.getStart(), bookingDtoRequest.getEnd(),
                    item, user, BookingStatus.WAITING);
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new RuntimeException("Вещь с Id " + itemId + " недоступна для бронирования!");
        }
    }

    @SuppressWarnings("checkstyle:Regexp")
    @Transactional
    @Override
    public BookingDto approvedBooking(long userId, long bookingId, boolean isApproved) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Запрос на бронирование с таким id не найден!"));
        if ((booking.getStatus().equals(BookingStatus.APPROVED) && isApproved) ||
                (booking.getStatus().equals(BookingStatus.REJECTED) && !isApproved)) {
            throw new BookingStateException("Статус уже установлен!");
        }

        if (booking.getItem().getOwner().getId() == userId) {
            booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            bookingRepository.save(booking);
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Согласовать запрос может только владелец вещи!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto findBooking(long userId, long bookingId) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Запрос на бронирование с таким id не найден!"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Поиск запроса на бронирование по id возможен только для автора запроса " +
                    " или для владельца вещи!");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findBookingByBookerId(long userId, String state, Pageable pageRequest) {
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING,
                                pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED,
                                pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new BookingStateException("Unknown state: " + state);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findBookingsByItemsOwnerId(long userId, String state, Pageable pageRequest) {
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemsOwnerId(userId, pageRequest).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository
                        .findAllCurrentByItemsOwnerId(userId, now, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAllPastByItemsOwnerId(userId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findAllFutureByItemsOwnerId(userId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllStatusByItemsOwnerId(userId, BookingStatus.WAITING, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllStatusByItemsOwnerId(userId, BookingStatus.REJECTED, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new BookingStateException("Unknown state: " + state);
        }
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден!");
        }
    }
}
