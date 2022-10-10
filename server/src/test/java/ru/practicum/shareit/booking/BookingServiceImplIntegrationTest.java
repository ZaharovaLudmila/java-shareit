package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=shareItBooking_test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {
    private final EntityManager em;
    private final BookingServiceImpl bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void addBooking() {
        User user1 = userRepository.save(new User(1L, "user1 Name", "user1@email.ru"));
        User user2 =  userRepository.save(new User(2L, "user2 Name", "user2@email.ru"));
        Item item = itemRepository.save(new Item(1, "item name",
                "item description", true, user1, null));
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

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
