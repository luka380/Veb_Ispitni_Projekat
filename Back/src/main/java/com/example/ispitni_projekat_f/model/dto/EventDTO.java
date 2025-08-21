package com.example.ispitni_projekat_f.model.dto;

import com.example.ispitni_projekat_f.model.entity.Event;
import com.example.ispitni_projekat_f.model.entity.RegisteredUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventDTO {
    public List<CommentDTO> comments;
    public UserEventStatusDTO userStatus;
    public EventStatsDTO eventStatus;
    private long id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    private LocalDateTime createdAt;
    @NotNull(message = "Start time is required")
    private LocalDateTime startsAt;
    @NotBlank(message = "Location is required")
    private String location;
    private UserDTO author;
    @NotNull(message = "Category is required")
    private CategoryDTO category;
    private int maxCapacity;
    @NotNull(message = "Tags are required")
    private List<String> tags;

    public static EventDTO fromEntity(Event event) {
        if (event == null) return null;
        EventDTO dto = new EventDTO();
        dto.id = event.getId();
        dto.title = event.getTitle();
        dto.description = event.getDescription();
        dto.createdAt = event.getCreatedAt();
        dto.startsAt = event.getStartsAt();
        dto.location = event.getLocation();
        dto.maxCapacity = event.getMaxOccupants();
        dto.tags = event.getTags().stream().toList();
        dto.author = UserDTO.fromEntity(event.getAuthor());
        dto.category = CategoryDTO.fromEntity(event.getCategory());
        dto.author.setPassword(null);
        return dto;
    }

    @JsonProperty("categoryId")
    public void setCategoryId(Long categoryId) {
        if (categoryId != null) {
            this.category = new CategoryDTO(categoryId);
        }
    }

    @JsonProperty("createdAt")
    public String getCreatedAtString() {
        return createdAt.toString();
    }

    @JsonProperty("startsAt")
    public String getStartsAtString() {
        return startsAt.toString();
    }

    public Event toEntity() {
        Event entity = new Event();
        entity.setId(this.id);
        entity.setTitle(this.title);
        entity.setDescription(this.description);
        entity.setCreatedAt(this.createdAt);
        entity.setStartsAt(this.startsAt);
        entity.setLocation(this.location);
        entity.setMaxOccupants(this.maxCapacity);
        entity.setTags(new HashSet<>(this.tags));
        entity.setAuthor(this.author != null ? (RegisteredUser) this.author.toEntity() : null);
        entity.setCategory(this.category != null ? this.category.toEntity() : null);
        entity.getAuthor().setPassword(null);
        return entity;
    }
}

