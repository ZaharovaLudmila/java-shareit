package ru.practicum.shareit.item.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private long id;
    @Column(name = "item_name", nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
