package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@email.com");
        itemRequest = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now(), List.of());
        itemRequestDto = new ItemRequestDto("Need a drill");
        itemRequestWithItemsDto = new ItemRequestWithItemsDto(1L, "Need a drill", LocalDateTime.now(), List.of());
    }

    @Test
    void create_shouldCreateItemRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestMapper.fromDto(any(ItemRequestDto.class), any(User.class), any(LocalDateTime.class)))
                .thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.toDtoWithItems(any(ItemRequest.class))).thenReturn(itemRequestWithItemsDto);

        ItemRequestWithItemsDto result = itemRequestService.create(1L, itemRequestDto);

        assertThat(result).isEqualTo(itemRequestWithItemsDto);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void create_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.create(1L, itemRequestDto));
    }

    @Test
    void getAllByUser_shouldReturnUserRequests() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(itemRequest));
        when(itemRequestMapper.toDtoList(anyList())).thenReturn(List.of(itemRequestWithItemsDto));

        List<ItemRequestWithItemsDto> result = itemRequestService.getAllByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(itemRequestWithItemsDto);
    }

    @Test
    void getAllByUser_whenUserNotFound_shouldThrowException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllByUser(1L));
    }

    @Test
    void getAll_shouldReturnOtherUsersRequests() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(eq(1L), any(PageRequest.class)))
                .thenReturn(List.of(itemRequest));
        when(itemRequestMapper.toDtoList(anyList())).thenReturn(List.of(itemRequestWithItemsDto));

        List<ItemRequestWithItemsDto> result = itemRequestService.getAll(1L, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(itemRequestWithItemsDto);
    }

    @Test
    void getById_shouldReturnRequest() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toDtoWithItems(any(ItemRequest.class))).thenReturn(itemRequestWithItemsDto);

        ItemRequestWithItemsDto result = itemRequestService.getById(1L, 1L);

        assertThat(result).isEqualTo(itemRequestWithItemsDto);
    }

    @Test
    void getById_whenRequestNotFound_shouldThrowException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getById(1L, 1L));
    }

    @Test
    void getById_whenUserNotFound_shouldThrowException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getById(1L, 1L));
    }

    @Test
    void getAll_withPagination_shouldUseCorrectPage() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(eq(1L), any(PageRequest.class)))
                .thenReturn(List.of(itemRequest));
        when(itemRequestMapper.toDtoList(anyList())).thenReturn(List.of(itemRequestWithItemsDto));

        itemRequestService.getAll(1L, 10, 5);

        verify(itemRequestRepository).findByRequesterIdNotOrderByCreatedDesc(eq(1L),
                argThat(page -> page.getPageNumber() == 2 && page.getPageSize() == 5));
    }
}
