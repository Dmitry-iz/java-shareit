//package ru.practicum.shareit.item;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.practicum.shareit.exception.BadRequestException;
//import ru.practicum.shareit.item.dto.CommentDto;
//import ru.practicum.shareit.item.dto.CreateItemRequestDto;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
//import ru.practicum.shareit.item.exception.ItemNotFoundException;
//import ru.practicum.shareit.item.exception.ItemNotOwnedByUserException;
//import ru.practicum.shareit.item.mapper.CommentMapper;
//import ru.practicum.shareit.item.mapper.ItemMapper;
//import ru.practicum.shareit.item.model.Comment;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.CommentRepository;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.item.service.ItemServiceImpl;
//import ru.practicum.shareit.user.exception.UserNotFoundException;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ItemServiceImplTest {
//
//    @Mock
//    private ItemRepository itemRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private ItemMapper itemMapper;
//
//    @Mock
//    private CommentRepository commentRepository;
//
//    @Mock
//    private CommentMapper commentMapper;
//
//    @InjectMocks
//    private ItemServiceImpl itemService;
//
//    private User owner;
//    private User otherUser;
//    private Item item;
//    private ItemDto itemDto;
//    private CreateItemRequestDto createItemDto;
//    private UpdateItemRequestDto updateItemDto;
//
//    @BeforeEach
//    void setUp() {
//        owner = new User(1L, "Owner", "owner@email.com");
//        otherUser = new User(2L, "Other", "other@email.com");
//        item = new Item(1L, "Item", "Description", true, owner, null);
//        itemDto = new ItemDto(1L, "Item", "Description", true, null, null, List.of());
//        createItemDto = new CreateItemRequestDto("Item", "Description", true, null);
//        updateItemDto = new UpdateItemRequestDto("Updated", "Updated Desc", false, null);
//    }
//
//    @Test
//    void createItem_shouldCreateItemSuccessfully() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
//        when(itemMapper.fromCreateDto(createItemDto, owner)).thenReturn(item);
//        when(itemRepository.save(item)).thenReturn(item);
//        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
//
//        ItemDto result = itemService.create(1L, createItemDto);
//
//        assertThat(result).isEqualTo(itemDto);
//        verify(itemRepository).save(item);
//    }
//
//    @Test
//    void createItem_withNonExistentUser_shouldThrowException() {
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> itemService.create(1L, createItemDto))
//                .isInstanceOf(UserNotFoundException.class);
//    }
//
//    @Test
//    void updateItem_shouldUpdateItemSuccessfully() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(itemRepository.save(item)).thenReturn(item);
//        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
//
//        ItemDto result = itemService.update(1L, 1L, updateItemDto);
//
//        assertThat(result).isEqualTo(itemDto);
//        verify(itemMapper).updateItemFromDto(updateItemDto, item);
//    }
//
//    @Test
//    void updateItem_withNonExistentItem_shouldThrowException() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
//        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> itemService.update(1L, 1L, updateItemDto))
//                .isInstanceOf(ItemNotFoundException.class);
//    }
//
//    @Test
//    void updateItem_byNonOwner_shouldThrowException() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//
//        assertThatThrownBy(() -> itemService.update(2L, 1L, updateItemDto))
//                .isInstanceOf(ItemNotOwnedByUserException.class);
//    }
//
//    @Test
//    void getItemById_shouldReturnItem() {
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
//
//        ItemDto result = itemService.getById(1L, 1L);
//
//        assertThat(result).isEqualTo(itemDto);
//    }
//
//    @Test
//    void getItemById_withNonExistentItem_shouldThrowException() {
//        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> itemService.getById(1L, 1L))
//                .isInstanceOf(ItemNotFoundException.class);
//    }
//
//    @Test
//    void getAllByOwner_shouldReturnItemsList() {
//        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
//        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
//
//        List<ItemDto> result = itemService.getAllByOwner(1L);
//
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0)).isEqualTo(itemDto);
//    }
//
//    @Test
//    void searchItems_shouldReturnSearchResults() {
//        when(itemRepository.search("item")).thenReturn(List.of(item));
//        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
//
//        List<ItemDto> result = itemService.search("item");
//
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0)).isEqualTo(itemDto);
//    }
//
//    @Test
//    void searchItems_withEmptyText_shouldReturnEmptyList() {
//        List<ItemDto> result = itemService.search("");
//
//        assertThat(result).isEmpty();
//        verifyNoInteractions(itemRepository);
//    }
//
////    @Test
////    void addComment_withoutBooking_shouldThrowException() {
////        CommentDto commentDto = new CommentDto("Great item!");
////        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
////        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
////
////        assertThatThrownBy(() -> itemService.addComment(1L, 1L, commentDto))
////                .isInstanceOf(BadRequestException.class)
////                .hasMessageContaining("User has not booked this item");
////    }
//}

package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingRepository bookingRepository; // Добавлен мок для BookingRepository

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User otherUser;
    private Item item;
    private ItemDto itemDto;
    private CreateItemRequestDto createItemDto;
    private UpdateItemRequestDto updateItemDto;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@email.com");
        otherUser = new User(2L, "Other", "other@email.com");
        item = new Item(1L, "Item", "Description", true, owner, null);
        itemDto = new ItemDto(1L, "Item", "Description", true, null, null, List.of());
        createItemDto = new CreateItemRequestDto("Item", "Description", true, null);
        updateItemDto = new UpdateItemRequestDto("Updated", "Updated Desc", false, null);
    }

    @Test
    void createItem_shouldCreateItemSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.fromCreateDto(createItemDto, owner)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.create(1L, createItemDto);

        assertThat(result).isEqualTo(itemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void createItem_withNonExistentUser_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(1L, createItemDto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateItem_shouldUpdateItemSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.update(1L, 1L, updateItemDto);

        assertThat(result).isEqualTo(itemDto);
        verify(itemMapper).updateItemFromDto(updateItemDto, item);
    }

    @Test
    void updateItem_withNonExistentItem_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(1L, 1L, updateItemDto))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void updateItem_byNonOwner_shouldThrowException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(2L, 1L, updateItemDto))
                .isInstanceOf(ItemNotOwnedByUserException.class);
    }

    @Test
    void getItemById_shouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        // Мокируем вызовы bookingRepository для владельца
        when(bookingRepository.findByItemIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(1L)).thenReturn(Collections.emptyList());
        when(itemMapper.toDtoWithBookings(item, Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
                .thenReturn(itemDto);

        ItemDto result = itemService.getById(1L, 1L);

        assertThat(result).isEqualTo(itemDto);
        verify(bookingRepository).findByItemIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Sort.class));
        verify(bookingRepository).findByItemIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class));
    }

    @Test
    void getItemById_byNonOwner_shouldReturnItemWithoutBookings() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(Collections.emptyList());
        // Для не-владельца bookings не должны вызываться
        when(itemMapper.toDtoWithBookings(item, null, null, Collections.emptyList()))
                .thenReturn(itemDto);

        ItemDto result = itemService.getById(1L, 2L); // Другой пользователь

        assertThat(result).isEqualTo(itemDto);
        verify(bookingRepository, never()).findByItemIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Sort.class));
        verify(bookingRepository, never()).findByItemIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class));
    }

    @Test
    void getItemById_withNonExistentItem_shouldThrowException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(1L, 1L))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void getAllByOwner_shouldReturnItemsList() {
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.getAllByOwner(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(itemDto);
    }

    @Test
    void searchItems_shouldReturnSearchResults() {
        when(itemRepository.search("item")).thenReturn(List.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.search("item");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(itemDto);
    }

    @Test
    void searchItems_withEmptyText_shouldReturnEmptyList() {
        List<ItemDto> result = itemService.search("");

        assertThat(result).isEmpty();
        verifyNoInteractions(itemRepository);
    }

//    @Test
//    void addComment_withoutBooking_shouldThrowException() {
//        CommentDto commentDto = new CommentDto("Great item!");
//        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(1L, 1L, LocalDateTime.now()))
//                .thenReturn(false);
//
//        assertThatThrownBy(() -> itemService.addComment(1L, 1L, commentDto))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessageContaining("User has not booked this item");
//    }
//
//    @Test
//    void addComment_withBooking_shouldAddCommentSuccessfully() {
//        CommentDto commentDto = new CommentDto("Great item!");
//        Comment comment = new Comment(1L, "Great item!", item, owner, LocalDateTime.now());
//        CommentDto savedCommentDto = new CommentDto(1L, "Great item!", "Owner", LocalDateTime.now());
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(1L, 1L, LocalDateTime.now()))
//                .thenReturn(true);
//        when(commentMapper.fromDto(commentDto, item, owner)).thenReturn(comment);
//        when(commentRepository.save(comment)).thenReturn(comment);
//        when(commentMapper.toDto(comment)).thenReturn(savedCommentDto);
//
//        CommentDto result = itemService.addComment(1L, 1L, commentDto);
//
//        assertThat(result).isEqualTo(savedCommentDto);
//        verify(commentRepository).save(comment);
//    }

//    @Test
//    void addComment_withoutBooking_shouldThrowException() {
//        CommentDto commentDto = new CommentDto(null, "Great item!", null, null);
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(1L, 1L, LocalDateTime.now()))
//                .thenReturn(false);
//
//        assertThatThrownBy(() -> itemService.addComment(1L, 1L, commentDto))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessageContaining("User has not booked this item");
//    }
//
//    @Test
//    void addComment_withBooking_shouldAddCommentSuccessfully() {
//        CommentDto commentDto = new CommentDto(null, "Great item!", null, null);
//        Comment comment = new Comment(1L, "Great item!", item, owner, LocalDateTime.now());
//        CommentDto savedCommentDto = new CommentDto(1L, "Great item!", "Owner", LocalDateTime.now());
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(1L, 1L, LocalDateTime.now()))
//                .thenReturn(true);
//        when(commentMapper.fromDto(commentDto, item, owner)).thenReturn(comment);
//        when(commentRepository.save(comment)).thenReturn(comment);
//        when(commentMapper.toDto(comment)).thenReturn(savedCommentDto);
//
//        CommentDto result = itemService.addComment(1L, 1L, commentDto);
//
//        assertThat(result).isEqualTo(savedCommentDto);
//        verify(commentRepository).save(comment);
//    }
}
