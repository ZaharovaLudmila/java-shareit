package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService requestService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    ItemRequest request;
    User user;
    ItemRequestDtoResponse requestDtoResponse;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "userName", "user@email.ru");
        requestDtoResponse = new ItemRequestDtoResponse(1, "request description",
                LocalDateTime.of(2022, 9, 16, 13, 22, 22), null);
    }

    @Test
    void addItemRequest() throws Exception {
        when(requestService.addItemRequest(anyLong(), any()))
                .thenReturn(requestDtoResponse);
        ItemRequestDto itemRequestDto = new ItemRequestDto(requestDtoResponse.getId(),
                requestDtoResponse.getDescription(), user,
                LocalDateTime.of(2022, 9, 16, 13, 22, 22));
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDtoResponse.getCreated().toString())));

        verify(requestService, times(1)).addItemRequest(anyLong(), any());
    }

    @Test
    void findAll() throws Exception {
        ItemRequestDtoResponse requestDtoResponse2 = new ItemRequestDtoResponse(2, "request2 description",
                LocalDateTime.of(2022, 9, 17, 13, 22, 22), null);
        when(requestService.findAll(anyLong(), any()))
                .thenReturn(List.of(requestDtoResponse, requestDtoResponse2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(requestService, times(1)).findAll(anyLong(), any());
    }

    @Test
    void findRequests() throws Exception {
        when(requestService.findAllByOwner(anyLong()))
                .thenReturn(List.of(requestDtoResponse));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDtoResponse.getCreated().toString())));

        verify(requestService, times(1)).findAllByOwner(anyLong());
    }

    @Test
    void findRequestById() throws Exception {
        when(requestService.findRequest(anyLong(), anyLong()))
                .thenReturn(requestDtoResponse);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDtoResponse.getCreated().toString())));

        verify(requestService, times(1)).findRequest(anyLong(), anyLong());
    }
}