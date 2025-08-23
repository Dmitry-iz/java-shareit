package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class ErrorHandlingIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getNonExistentUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getNonExistentItem_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/items/999")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUserWithDuplicateEmail_ShouldReturnConflict() throws Exception {
        String userJson = """
                {
                    "name": "Duplicate",
                    "email": "user1@email.com"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateItemAsNonOwner_ShouldReturnForbidden() throws Exception {
        String updateJson = """
                {
                    "name": "Unauthorized Update"
                }
                """;

        mockMvc.perform(patch("/items/{itemId}", item1.getId())
                        .header("X-Sharer-User-Id", user2.getId()) // Not the owner
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createBookingForOwnItem_ShouldReturnForbidden() throws Exception {
        String bookingJson = String.format("""
                        {
                            "itemId": %d,
                            "start": "%s",
                            "end": "%s"
                        }
                        """, item1.getId(),
                LocalDateTime.now().plusDays(1).toString(),
                LocalDateTime.now().plusDays(2).toString());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void approveBookingAsNonOwner_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", booking2.getId())
                        .header("X-Sharer-User-Id", user2.getId()) // Not the owner
                        .param("approved", "true"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());
    }
}
