package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@Transactional
@SpringBootTest(
        properties = "db.name=shareIt_test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final BookingServiceImpl bookingService;

    @Test
    void addBooking() {
        User user1 = new User(1, "user1 Name", "user1@email.ru");
        User user2 = new User(2, "user2 Name", "user2@email.ru");
        Item item = new Item(1, "item name", "item description", true, user1, null);
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        em.createNativeQuery("insert into users (user_name, email) values (?, ?)")
                .setParameter(1, user1.getName())
                .setParameter(2, user1.getEmail())
                .executeUpdate();

        em.createNativeQuery("insert into users (user_name, email) values (?, ?)")
                .setParameter(1, user2.getName())
                .setParameter(2, user2.getEmail())
                .executeUpdate();

        em.createNativeQuery("insert into items (item_name, description, is_available, owner_id) values (?, ?, ?,?)")
                .setParameter(1, item.getName())
                .setParameter(2, item.getDescription())
                .setParameter(3, item.getAvailable())
                .setParameter(4, user1.getId())
                .executeUpdate();

        bookingService.addBooking(user2.getId(), item.getId(), bookingDtoRequest);
        TypedQuery<Booking> query =
                em.createQuery("Select b from Booking b where b.booker.id = :booker_id", Booking.class);
        Booking booking = query.setParameter("booker_id", user2.getId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDtoRequest.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDtoRequest.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingDtoRequest.getItemId()));
    }

}