package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item addItem(User user, Item item);

    Item update(long userId, Item item);

    Optional<Item> findItem(long itemId);

    List<Item> findAll(long userId);

    List<Item> searchItems(String text);
}
