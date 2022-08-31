package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BOOKINGS", schema = "public")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOOKING_ID", nullable = false)
    private long id;
    @Column(name = "START_DATE")
    private LocalDateTime start;
    @Column(name = "END_DATE")
    private LocalDateTime end;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "ITEM_ID")
    private Item item;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "BOOKER_ID")
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
