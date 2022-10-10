package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=shareIt_test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplIntegrationTest {
    private final EntityManager em;
    private final ItemRequestServiceImpl requestService;
    private final UserRepository userRepository;

    @Test
    void addItemRequest() {
        User user = userRepository.save(new User(1, "user Name", "user@email.ru"));

        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "request description", user, null);
        requestService.addItemRequest(user.getId(), itemRequestDto);

        TypedQuery<ItemRequest> query =
                em.createQuery("Select r from ItemRequest r where r.requester.id = :requester_id", ItemRequest.class);
        ItemRequest request = query.setParameter("requester_id", user.getId()).getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(request.getRequester(), equalTo(itemRequestDto.getRequester()));
        assertThat(request.getCreated(), notNullValue());
    }
}
