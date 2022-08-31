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
@Table(name = "COMMENTS", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID", nullable = false)
    private long id;
    @Column(nullable = false)
    private String text;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "ITEM_ID")
    private Item item;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "AUTHOR_ID")
    private User author;
    private LocalDateTime created;
}
