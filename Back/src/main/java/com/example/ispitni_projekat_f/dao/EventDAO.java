package com.example.ispitni_projekat_f.dao;

import com.example.ispitni_projekat_f.model.entity.Event;
import jakarta.ejb.Stateless;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class EventDAO extends SimpleAbstractDAO<Event, Long> {

    protected EventDAO() {
        super(Event.class);
    }

    public List<Event> getEventsBySearchCategoryIdTag(int page, int size, String search, Long categoryId, String tag) {
        return em.createQuery("SELECT e " +
                        "FROM Event e " +
                        "WHERE (:search IS NULL OR :search = '' OR (LOWER(e.title) LIKE LOWER(CONCAT(:search, '%'))) OR (CONCAT(' ', LOWER(e.description)) LIKE CONCAT('% ', LOWER(:search), '%'))) " +
                        "AND (:categoryId IS NULL OR :categoryId = -1 OR e.category.id = :categoryId) " +
                        "AND (:tag IS NULL OR :tag = '' OR (:tag MEMBER OF e.tags)) " +
                        "ORDER BY e.id DESC", Event.class)
                .setParameter("search", search)
                .setParameter("categoryId", categoryId)
                .setParameter("tag", tag)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Event> getTopViewedEvents(int page, int size, LocalDateTime since) {
        return em.createQuery("SELECT e " +
                        "FROM Event e LEFT JOIN UserEventTracking uet ON e.id = uet.combinedKey.eventId AND e.createdAt >= :since " +
                        "GROUP BY e ORDER BY COUNT(uet.id) DESC", Event.class)
                .setParameter("since", since)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Event> getTopReactedEvents(int page, int size) {
        return em.createQuery("SELECT e " +
                        "FROM Event e LEFT JOIN UserEventTracking uet ON e.id = uet.combinedKey.eventId AND (uet.reaction = 'LIKE' OR uet.reaction = 'DISLIKE') " +
                        "GROUP BY e ORDER BY COUNT(uet.id) DESC", Event.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}
