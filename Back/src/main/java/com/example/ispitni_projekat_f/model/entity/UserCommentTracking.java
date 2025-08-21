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
public class UserCommentTracking {
    @EmbeddedId
    private UserCommentTracking.CombinedKey combinedKey;
    @ManyToOne
    @MapsId("commentId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;
    @ManyToOne
    @MapsId("userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BaseUser user;
    @Enumerated(EnumType.STRING)
    private UserReaction reaction;

    @Embeddable
    public static class CombinedKey {
        private long userId;
        private long commentId;

        public CombinedKey(long userId, long commentId) {
            this.userId = userId;
            this.commentId = commentId;
        }

        public CombinedKey() {
        }
    }
}
