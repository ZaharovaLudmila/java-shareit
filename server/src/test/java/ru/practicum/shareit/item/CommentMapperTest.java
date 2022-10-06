package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toCommentDtoTest() {
        User user = new User(1, "userName", "user@email.ru");
        Item item = new Item(1, "itemName", "item description", true, user, null);
        Comment comment = new Comment(1, "text comment", item, user,
                LocalDateTime.of(2022, 9, 16, 13, 44, 44));
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        assertNotNull(commentDto);
        assertEquals(CommentDto.class, commentDto.getClass());
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
    }
}