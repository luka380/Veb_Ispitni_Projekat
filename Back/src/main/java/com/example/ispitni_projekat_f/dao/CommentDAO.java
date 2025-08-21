package com.example.ispitni_projekat_f.dao;

import com.example.ispitni_projekat_f.model.entity.Comment;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class CommentDAO extends SimpleAbstractDAO<Comment, Long> {

    protected CommentDAO() {
        super(Comment.class);
    }

    public List<Comment> findByEventId(Long eventId) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.event.id = :eventId", Comment.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }
}
