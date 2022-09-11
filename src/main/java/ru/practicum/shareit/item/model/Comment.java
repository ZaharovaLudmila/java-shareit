package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private long id;
    @Column(nullable = false)
    private String text;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "author_id")
    private User author;
    private LocalDateTime created;
}
