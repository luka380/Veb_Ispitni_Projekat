package com.example.ispitni_projekat_f.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class UserEventTracking {
    @EmbeddedId
    private CombinedKey combinedKey;
    @ManyToOne
    @MapsId("eventId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;
    @ManyToOne
    @MapsId("userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BaseUser user;
    @Enumerated(EnumType.STRING)
    private UserReaction reaction;

    @Embeddable
    public static class CombinedKey {
        private long userId;
        private long eventId;

        public CombinedKey(long userId, long eventId) {
            this.userId = userId;
            this.eventId = eventId;
        }

        public CombinedKey() {
        }
    }
}
