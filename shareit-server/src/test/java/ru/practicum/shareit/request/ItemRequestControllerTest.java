package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ru.practicum.shareit.exception.BadRequestException;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestWithItemsDto itemRequestWithItemsDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto("Need a drill");

        itemRequestWithItemsDto = new ItemRequestWithItemsDto(
                1L,
                "Need a drill",
                LocalDateTime.now(),
                List.of()
        );
    }

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        when(itemRequestService.create(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestWithItemsDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getAllByUser_shouldReturnUserRequests() throws Exception {
        when(itemRequestService.getAllByUser(anyLong()))
                .thenReturn(List.of(itemRequestWithItemsDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));
    }

    @Test
    void getAll_shouldReturnOtherUsersRequests() throws Exception {
        when(itemRequestService.getAll(anyLong(), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(itemRequestWithItemsDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getById_shouldReturnRequest() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequestWithItemsDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void create_withoutUserIdHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenDescriptionIsBlank_shouldReturnBadRequest() throws Exception {
        ItemRequestDto invalidRequest = new ItemRequestDto("");

        // Мокируем сервис, чтобы он бросил исключение
        when(itemRequestService.create(anyLong(), any(ItemRequestDto.class)))
                .thenThrow(new BadRequestException("Description cannot be blank"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_withInvalidPagination_shouldReturnBadRequest() throws Exception {

        when(itemRequestService.getAll(anyLong(), eq(-1), any(Integer.class)))
                .thenThrow(new BadRequestException("Invalid pagination parameters"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }
}
