package ru.practicum.shareit.requests.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private long id;
    @Column(nullable = false)
    private String description;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "requester_id")
    private User requester;
    @Column(name = "created_date")
    private LocalDateTime created;
}
