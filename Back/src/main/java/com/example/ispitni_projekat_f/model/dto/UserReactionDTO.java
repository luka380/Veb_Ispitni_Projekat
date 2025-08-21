package com.example.ispitni_projekat_f.model.dto;

import com.example.ispitni_projekat_f.model.entity.UserReaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.example.ispitni_projekat_f.model.dto.UserReactionDTO.UserReactionDTOEnum.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserReactionDTO {
    private UserReactionDTOEnum reaction;

    public static UserReactionDTO fromUserReaction(UserReaction userReaction) {
        return new UserReactionDTO(switch (userReaction) {
            case LIKE -> LIKE;
            case DISLIKE -> DISLIKE;
            case NO_ACTION -> NO_REACTION;
            case null -> NO_VIEW;
        });
    }

    public UserReaction toUserReaction() {
        return switch (this.reaction) {
            case LIKE -> UserReaction.LIKE;
            case DISLIKE -> UserReaction.DISLIKE;
            case NO_REACTION -> UserReaction.NO_ACTION;
            case NO_VIEW -> null;
        };
    }

    public enum UserReactionDTOEnum {
        NO_VIEW,
        NO_REACTION,
        LIKE,
        DISLIKE
    }
}
