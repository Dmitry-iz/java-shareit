package ru.practicum.shareit.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemRequestDto {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 1000)
    private String description;

    @NotNull(message = "Available status must be provided")
    private Boolean available;

    private Long requestId;
}