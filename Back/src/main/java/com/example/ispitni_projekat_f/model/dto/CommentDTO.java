package com.example.ispitni_projekat_f.model.dto;

import com.example.ispitni_projekat_f.model.entity.Comment;
import com.example.ispitni_projekat_f.model.entity.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private long id;
    @NotNull(message = "eventId is mandatory")
    private long eventId;
    @NotBlank(message = "Author must not be empty")
    private String authorName;
    @NotBlank(message = "Content must not be empty")
    private String text;
    private LocalDateTime createdAt;

    public static CommentDTO fromEntity(Comment comment) {
        if (comment == null) return null;
        CommentDTO dto = new CommentDTO();
        dto.id = comment.getId();
        dto.authorName = comment.getAuthor();
        dto.text = comment.getContent();
        dto.createdAt = comment.getCreatedAt();
        dto.eventId = comment.getEvent().getId();
        return dto;
    }

    @JsonProperty("createdAt")
    public String getCreatedAtString() {
        return createdAt.toString();
    }

    public Comment toEntity() {
        Comment entity = new Comment();
        entity.setId(this.id);
        entity.setAuthor(this.authorName);
        entity.setContent(this.text);
        entity.setCreatedAt(this.createdAt);
        entity.setEvent(new Event(eventId));
        return entity;
    }
}

