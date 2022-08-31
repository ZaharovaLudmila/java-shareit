package ru.practicum.shareit.requests.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "REQUESTS", schema = "public")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID", nullable = false)
    private long id;
    @NotBlank
    @Column(nullable = false)
    private String description;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "REQUESTER_ID")
    private User requester;
    @Column(name = "CREATED_DATE")
    private LocalDateTime created;
}
