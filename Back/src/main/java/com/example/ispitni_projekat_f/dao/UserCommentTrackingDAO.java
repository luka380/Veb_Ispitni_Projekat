package com.example.ispitni_projekat_f.dao;

import com.example.ispitni_projekat_f.model.dto.CommentStats;
import com.example.ispitni_projekat_f.model.entity.*;
import jakarta.ejb.Stateless;

@Stateless
public class UserCommentTrackingDAO extends SimpleAbstractDAO<UserCommentTracking, UserCommentTracking.CombinedKey>{

    protected UserCommentTrackingDAO() {
        super(UserCommentTracking.class);
    }

    public UserReaction upsertCommentReaction(Long userId, Long commentId, UserReaction type) {
        UserCommentTracking uct = new UserCommentTracking();
        uct.setCombinedKey(new UserCommentTracking.CombinedKey(userId, commentId));
        uct.setComment(em.getReference(Comment.class, commentId));
        uct.setUser(em.getReference(RegisteredUser.class, userId));
        uct.setReaction(type);
        return em.merge(uct).getReaction();
    }

    public CommentStats getCommentStats(Long commentId, Long userId){
        CommentStats stats = em.createQuery("SELECT " +
                "SUM(CASE WHEN uct.reaction = 'LIKE' THEN 1 ELSE 0 END) as likes, " +
                "SUM(CASE WHEN uct.reaction = 'DISLIKE' THEN 1 ELSE 0 END) as dislikes, " +
                "CASE WHEN SUM(CASE WHEN (uct.user.id = :userId AND uct.reaction = 'LIKE') THEN 1 ELSE 0 END) > 0 THEN TRUE ELSE FALSE END as isLiked, " +
                "CASE WHEN SUM(CASE WHEN (uct.user.id = :userId AND uct.reaction = 'DISLIKE') THEN 1 ELSE 0 END) > 0 THEN TRUE ELSE FALSE END as isDisliked " +
                "FROM UserCommentTracking uct " +
                "WHERE uct.combinedKey.commentId = :commentId", CommentStats.class)
                .setParameter("commentId", commentId)
                .setParameter("userId", userId)
                .getSingleResult();
        return stats;
    }
}
