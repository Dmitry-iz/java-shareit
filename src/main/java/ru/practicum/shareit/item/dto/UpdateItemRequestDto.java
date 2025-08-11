package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemRequestDto {
    @Size(max = 255)
    private String name;
    @Size(max = 1000)
    private String description;
    private Boolean available;
    private Long requestId;
}