package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.CreateUserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    private Long userId;
    private Long secondUserId;

    @BeforeEach
    void setUp() {
        // Создаем первого пользователя
        CreateUserRequestDto userDto = new CreateUserRequestDto("User 1", "user1@email.com");
        var createdUser = userService.create(userDto);
        userId = createdUser.getId();

        // Создаем второго пользователя
        CreateUserRequestDto secondUserDto = new CreateUserRequestDto("User 2", "user2@email.com");
        var secondCreatedUser = userService.create(secondUserDto);
        secondUserId = secondCreatedUser.getId();
    }

    @Test
    void getAllByUser_shouldReturnUserRequests() {
        // Given
        ItemRequestDto request1 = new ItemRequestDto("Need a drill");
        ItemRequestDto request2 = new ItemRequestDto("Need a hammer");

        itemRequestService.create(userId, request1);
        itemRequestService.create(userId, request2);

        // When
        List<ItemRequestWithItemsDto> userRequests = itemRequestService.getAllByUser(userId);

        // Then
        assertThat(userRequests).hasSize(2);
        assertThat(userRequests).extracting(ItemRequestWithItemsDto::getDescription)
                .containsExactlyInAnyOrder("Need a drill", "Need a hammer");
    }

    @Test
    void getAll_shouldReturnOtherUsersRequests() {
        // Given
        // Запрос от первого пользователя
        ItemRequestDto request1 = new ItemRequestDto("Need a drill");
        itemRequestService.create(userId, request1);

        // Запрос от второго пользователя
        ItemRequestDto request2 = new ItemRequestDto("Need a hammer");
        itemRequestService.create(secondUserId, request2);

        // When - получаем запросы для первого пользователя (должны видеть только запросы второго)
        List<ItemRequestWithItemsDto> otherRequests = itemRequestService.getAll(userId, 0, 10);

        // Then
        assertThat(otherRequests).hasSize(1);
        assertThat(otherRequests.get(0).getDescription()).isEqualTo("Need a hammer");
    }

    @Test
    void getById_shouldReturnRequest() {
        // Given
        ItemRequestDto requestDto = new ItemRequestDto("Need a drill");
        ItemRequestWithItemsDto createdRequest = itemRequestService.create(userId, requestDto);

        // When
        ItemRequestWithItemsDto foundRequest = itemRequestService.getById(userId, createdRequest.getId());

        // Then
        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(createdRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void getById_whenRequestNotFound_shouldThrowException() {
        // When & Then
        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getById(userId, 999L));
    }

    @Test
    void getAll_withPagination_shouldReturnPaginatedResults() {
        // Given - создаем 3 запроса от второго пользователя
        for (int i = 1; i <= 3; i++) {
            ItemRequestDto request = new ItemRequestDto("Request " + i);
            itemRequestService.create(secondUserId, request);
        }

        // When - получаем первую страницу с 2 элементами
        List<ItemRequestWithItemsDto> firstPage = itemRequestService.getAll(userId, 0, 2);

        // Then
        assertThat(firstPage).hasSize(2);

        // When - получаем вторую страницу с 2 элементами
        List<ItemRequestWithItemsDto> secondPage = itemRequestService.getAll(userId, 2, 2);

        // Then
        assertThat(secondPage).hasSize(1);
    }
}