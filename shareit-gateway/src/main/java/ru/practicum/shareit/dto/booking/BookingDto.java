//package ru.practicum.shareit.dto.booking;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class BookingDto {
//    private Long id;
//    private LocalDateTime start;
//    private LocalDateTime end;
//    private BookingStatus status;
//    private BookerDto booker;
//    private ItemDto item;
//
//    @Data
//    public static class BookerDto {
//        private Long id;
//        private String name;
//    }
//
//    @Data
//    public static class ItemDto {
//        private Long id;
//        private String name;
//    }
//}