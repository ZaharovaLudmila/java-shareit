package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRepositoryMemory implements ItemRepository {

    private final Map<Long, Item> itemList = new HashMap<>();
    private Long itemId = 0L;

    @Override
    public Item addItem(User user, Item item) {
        item.setId(++itemId);
        item.setOwner(user);
        itemList.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long userId, Item item) {
        Item oldItem = itemList.get(item.getId());
        if (oldItem != null) {
            if (oldItem.getOwner().getId() == userId) {
                if (item.getName() != null && !(item.getName().trim().isBlank())) {
                    oldItem.setName(item.getName());
                }
                if (item.getDescription() != null && !(item.getDescription().trim().isBlank())) {
                    oldItem.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    oldItem.setAvailable(item.getAvailable());
                }
                itemList.put(oldItem.getId(), oldItem);
                return oldItem;
            } else {
                throw new NotFoundException("Пользователь с id " + userId + " не является владельцем данной вещи!");
            }
        } else {
            throw new NotFoundException("Вещь не найдена!");
        }
    }

    @Override
    public Optional<Item> findItem(long itemId) {
        if (itemList.containsKey(itemId)) {
            return Optional.of(itemList.get(itemId));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(long userId) {
        return itemList.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemList.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable())
                .collect(Collectors.toList());
    }
}
