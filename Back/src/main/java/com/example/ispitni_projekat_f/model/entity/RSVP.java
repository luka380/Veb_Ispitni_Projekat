package com.example.ispitni_projekat_f.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class RSVP {
    @EmbeddedId
    private CombinedKey combinedKey;
    @ManyToOne
    @MapsId("eventId")
    private Event event;

    @Embeddable
    public static class CombinedKey {
        private String email;
        private long eventId;

        public CombinedKey(String email, long eventId) {
            this.email = email;
            this.eventId = eventId;
        }

        public CombinedKey() {
        }
    }
}
