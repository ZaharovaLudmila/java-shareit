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
@Table(name = "ITEMS", schema = "public")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID", nullable = false)
    private long id;
    @Column(name = "ITEM_NAME", nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(name = "IS_AVAILABLE", nullable = false)
    private Boolean available;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "OWNER_ID")
    private User owner;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "REQUEST_ID")
    private ItemRequest request;
}
