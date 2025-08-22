//package ru.practicum.shareit.client;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//import ru.practicum.shareit.dto.item.CommentDto;
//import ru.practicum.shareit.dto.item.CreateItemRequestDto;
//import ru.practicum.shareit.dto.item.UpdateItemRequestDto;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ItemClientTest {
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @InjectMocks
//    private ItemClient itemClient;
//
//    private final String serverUrl = "http://localhost:8080";
//
//    @BeforeEach
//    void setUp() {
//        itemClient = new ItemClient(serverUrl, restTemplate);
//    }
//
//    @Test
//    void createItem_ShouldCallPost() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        CreateItemRequestDto requestDto = new CreateItemRequestDto();
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = itemClient.createItem(1L, requestDto);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                eq(serverUrl + "/items"),
//                eq(org.springframework.http.HttpMethod.POST),
//                any(),
//                eq(Object.class)
//        );
//    }
//
//    @Test
//    void updateItem_ShouldCallPatch() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        UpdateItemRequestDto requestDto = new UpdateItemRequestDto();
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = itemClient.updateItem(1L, 123L, requestDto);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                contains("/items/123"),
//                eq(org.springframework.http.HttpMethod.PATCH),
//                any(),
//                eq(Object.class)
//        );
//    }
//
//    @Test
//    void getItem_ShouldCallGet() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = itemClient.getItem(1L, 123L);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                contains("/items/123"),
//                eq(org.springframework.http.HttpMethod.GET),
//                any(),
//                eq(Object.class)
//        );
//    }
//
//    @Test
//    void getUsersItems_ShouldCallGet() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = itemClient.getUsersItems(1L);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                eq(serverUrl + "/items"),
//                eq(org.springframework.http.HttpMethod.GET),
//                any(),
//                eq(Object.class)
//        );
//    }
//
//    @Test
//    void searchItems_ShouldCallGetWithParameters() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = itemClient.searchItems("test", 1L);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                contains("/items/search?text=test"),
//                eq(org.springframework.http.HttpMethod.GET),
//                any(),
//                eq(Object.class),
//                anyMap()
//        );
//    }
//
//    @Test
//    void addComment_ShouldCallPost() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        CommentDto commentDto = new CommentDto();
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = itemClient.addComment(1L, 123L, commentDto);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                contains("/items/123/comment"),
//                eq(org.springframework.http.HttpMethod.POST),
//                any(),
//                eq(Object.class)
//        );
//    }
//}
