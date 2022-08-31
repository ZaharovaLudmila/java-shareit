package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //All by Booker ID
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    //Current by Booker ID
    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId,
                                                                                 LocalDateTime nowStart,
                                                                                 LocalDateTime nowEnd);

    //Past by Booker ID
    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime now);

    //FUTURE by Booker ID
    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime now);

    //Status by Booker ID
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, BookingStatus status);

    //All by item owner ID
    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findAllByItemsOwnerId(long userId);

    //Current by item owner ID
    @Query("select b from Booking b where b.item.owner.id = ?1 and " +
            "b.start <= ?2 and b.end >= ?3 order by b.start desc")
    List<Booking> findAllCurrentByItemsOwnerId(long userId, LocalDateTime nowStart, LocalDateTime nowEnd);

    //Past by item owner ID
    @Query("select b from Booking b where b.item.owner.id = ?1 and " +
            "b.end <= ?2 order by b.start desc")
    List<Booking> findAllPastByItemsOwnerId(long userId, LocalDateTime now);

    //FUTURE by item owner ID
    @Query("select b from Booking b where b.item.owner.id = ?1 and " +
            "b.start >= ?2 order by b.start desc")
    List<Booking> findAllFutureByItemsOwnerId(long userId, LocalDateTime now);

    //Status by item owner ID
    @Query("select b from Booking b where b.item.owner.id = ?1 and " +
            "b.status = ?2 order by b.start desc")
    List<Booking> findAllStatusByItemsOwnerId(long userId, BookingStatus status);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2 and b.end <= ?3")
    List<Booking> findAllByBookerIdAAndItemId(long userId, long itemId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id = ?1")
    List<Booking> findAllByItemsId(long itemId);

    @Query("select b from Booking b where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and b.start <= ?3 order by b.end desc ")
    List<Booking> findLastBookingByItemId(long itemId, long userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and b.start >= ?3 order by b.start asc")
    List<Booking> findNextBookingByItemId(long itemId, long userId, LocalDateTime now);
}
