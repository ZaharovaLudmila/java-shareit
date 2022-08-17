package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserService userService;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User user = UserMapper.toUser(checkUser(userId));
        return ItemMapper.toItemDto(itemDao.addItem(user, ItemMapper.toItem(itemDto, user)));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        User user = UserMapper.toUser(checkUser(userId));
        return ItemMapper.toItemDto(itemDao.update(userId, ItemMapper.toItem(itemDto, user)));
    }

    @Override
    public ItemDto findItem(long itemId) {
        return ItemMapper.toItemDto(itemDao.findItem(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с таким id не найдена!")));
    }

    @Override
    public List<ItemDto> findAll(long userId) {
        checkUser(userId);
        return itemDao.findAll(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemDao.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private UserDto checkUser(long userId) {
        return userService.getUserById(userId);
    }
}
