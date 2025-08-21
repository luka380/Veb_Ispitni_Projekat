package com.example.ispitni_projekat_f.dao;

import com.example.ispitni_projekat_f.model.dto.EventStatsDTO;
import com.example.ispitni_projekat_f.model.dto.UserEventStatusDTO;
import com.example.ispitni_projekat_f.model.dto.UserReactionDTO;
import com.example.ispitni_projekat_f.model.entity.Event;
import com.example.ispitni_projekat_f.model.entity.RegisteredUser;
import com.example.ispitni_projekat_f.model.entity.UserEventTracking;
import com.example.ispitni_projekat_f.model.entity.UserReaction;
import jakarta.ejb.Stateless;

import static com.example.ispitni_projekat_f.model.dto.UserReactionDTO.UserReactionDTOEnum.*;

@Stateless
public class UserEventTrackingDAO extends SimpleAbstractDAO<UserEventTracking, UserEventTracking.CombinedKey> {

    protected UserEventTrackingDAO() {
        super(UserEventTracking.class);
    }

    public EventStatsDTO getEventStats(Long eventId) {
        Object[] row = (Object[]) em.createQuery("SELECT SUM(CASE WHEN uet.reaction = :like THEN 1 ELSE 0 END), SUM(CASE WHEN uet.reaction = :dislike THEN 1 ELSE 0 END), COUNT(uet) FROM UserEventTracking uet WHERE uet.combinedKey.eventId = :eventId").
                setParameter("eventId", eventId).
                setParameter("like", UserReaction.LIKE).
                setParameter("dislike", UserReaction.DISLIKE).
                getSingleResult();

        long likes = row[0] == null ? 0L : (Long) row[0];
        long dislikes = row[1] == null ? 0L : (Long) row[1];
        long views = row[2] == null ? 0L : (Long) row[2];

        return new EventStatsDTO(eventId, likes, dislikes, views);
    }

    public UserEventStatusDTO getUserEventStatus(Long userId, Long eventId) {
        UserEventTracking uet = em.find(UserEventTracking.class, new UserEventTracking.CombinedKey(userId, eventId));
        UserReactionDTO userReactionDTO;

        if (uet != null) {
            switch (uet.getReaction()) {
                case LIKE:
                    userReactionDTO = new UserReactionDTO(LIKE);
                    break;
                case DISLIKE:
                    userReactionDTO = new UserReactionDTO(DISLIKE);
                    break;
                default:
                    userReactionDTO = new UserReactionDTO(NO_REACTION);
            }
        } else {
            userReactionDTO = new UserReactionDTO(NO_VIEW);
        }

        return new UserEventStatusDTO(userId, eventId, userReactionDTO);
    }

    public UserReaction upsertReaction(Long eventId, Long userId, UserReaction type) {
        UserEventTracking uet = new UserEventTracking();
        uet.setCombinedKey(new UserEventTracking.CombinedKey(userId, eventId));
        uet.setEvent(em.getReference(Event.class, eventId));
        uet.setUser(em.getReference(RegisteredUser.class, userId));
        uet.setReaction(type);
        return em.merge(uet).getReaction();
    }

}
