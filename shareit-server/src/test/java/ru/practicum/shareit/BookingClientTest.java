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
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class BookingClientTest {
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @InjectMocks
//    private BookingClient bookingClient;
//
//    private final String serverUrl = "http://localhost:8080";
//
//    @BeforeEach
//    void setUp() {
//        bookingClient = new BookingClient(serverUrl, restTemplate);
//    }
//
//    @Test
//    void getBookings_ShouldCallGetWithParameters() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = bookingClient.getBookings(1L, "ALL", 0, 10);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                contains("/bookings?state=ALL&from=0&size=10"),
//                eq(org.springframework.http.HttpMethod.GET),
//                any(),
//                eq(Object.class),
//                anyMap()
//        );
//    }
//
//    @Test
//    void getAllByOwner_ShouldCallGetWithParameters() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = bookingClient.getAllByOwner(1L, "ALL", 0, 10);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                contains("/bookings/owner?state=ALL&from=0&size=10"),
//                eq(org.springframework.http.HttpMethod.GET),
//                any(),
//                eq(Object.class),
//                anyMap()
//        );
//    }
//
//    @Test
//    void bookItem_ShouldCallPost() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        Object requestDto = new Object();
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = bookingClient.bookItem(1L, requestDto);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                eq(serverUrl + "/bookings"),
//                eq(org.springframework.http.HttpMethod.POST),
//                any(),
//                eq(Object.class)
//        );
//    }
//
//    @Test
//    void getBooking_ShouldCallGetWithPath() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = bookingClient.getBooking(1L, 123L);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                contains("/bookings/123"),
//                eq(org.springframework.http.HttpMethod.GET),
//                any(),
//                eq(Object.class)
//        );
//    }
//
//    @Test
//    void approveBooking_ShouldCallPatchWithParameters() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = bookingClient.approveBooking(1L, 123L, true);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                contains("/bookings/123?approved=true"),
//                eq(org.springframework.http.HttpMethod.PATCH),
//                any(),
//                eq(Object.class),
//                anyMap()
//        );
//    }
//
//    @Test
//    void cancelBooking_ShouldCallPatch() {
//        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("test");
//        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
//                .thenReturn(expectedResponse);
//
//        ResponseEntity<Object> response = bookingClient.cancelBooking(1L, 123L);
//
//        assertEquals(expectedResponse, response);
//        verify(restTemplate).exchange(
//                contains("/bookings/123/cancel"),
//                eq(org.springframework.http.HttpMethod.PATCH),
//                any(),
//                eq(Object.class)
//        );
//    }
//}