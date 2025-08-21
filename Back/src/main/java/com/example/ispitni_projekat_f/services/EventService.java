package com.example.ispitni_projekat_f.services;

import com.example.ispitni_projekat_f.dao.*;
import com.example.ispitni_projekat_f.model.dto.*;
import com.example.ispitni_projekat_f.model.entity.Event;
import com.example.ispitni_projekat_f.model.entity.RSVP;
import com.example.ispitni_projekat_f.model.entity.UserReaction;
import com.example.ispitni_projekat_f.security.AppSecurityContext;
import com.example.ispitni_projekat_f.utils.CurrentUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventService {
    @Inject
    private EventDAO eventDAO;
    @Inject
    private UserEventTrackingDAO userEventTrackingDAO;
    @Inject
    private CommentDAO commentDAO;
    @Inject
    private RedisDAO redisDAO;
    @Inject
    private RSVPDAO rsvpDAO;
    @Inject
    private UserCommentTrackingDAO userCommentTrackingDAO;
    @Inject
    private CurrentUser currentUser;

    public List<EventDTO> getEvents(int page, int size, String search, Long categoryId, String tag) {
        return eventDAO.getEventsBySearchCategoryIdTag(page, size, search, categoryId, tag).stream().map(EventDTO::fromEntity).collect(Collectors.toList());
    }

    public List<EventDTO> getEvents(int page, int size) {
        return getEvents(page, size, null, null, null);
    }

    @Transactional
    public EventDTO createEvent(EventDTO event) {
        event.setCreatedAt(LocalDateTime.now());
        event.setAuthor(currentUser.get());
        return EventDTO.fromEntity(eventDAO.insert(event.toEntity()));
    }

    @Transactional
    public EventDTO updateEvent(Long id, EventDTO event) {
        event.setId(id);
        event.setCreatedAt(getEvent(id, currentUser.get().getId()).getCreatedAt());
        event.setAuthor(currentUser.get());
        return EventDTO.fromEntity(eventDAO.update(event.toEntity()));
    }

    @Transactional
    private EventDTO getEvent(Long eventId, Long userId) {
        EventDTO event = EventDTO.fromEntity(eventDAO.findById(eventId));
        if (event == null)
            return null;

        event.userStatus = userEventTrackingDAO.getUserEventStatus(userId, eventId);
        EventStatsDTO stats = redisDAO.getEventStats(eventId);
        if (stats != null) {
            System.out.println("Cache HIT!");
            event.eventStatus = stats;
            return event;
        }

        System.out.println("Cache MISS!");
        stats = userEventTrackingDAO.getEventStats(eventId);
        event.eventStatus = stats;

        redisDAO.cacheEventStats(eventId, stats);
        return event;
    }

    @Transactional
    public EventDTO deleteEvent(Long id) {
        return EventDTO.fromEntity(eventDAO.delete(id));
    }

    public List<CommentDTO> getEventComments(Long eventId, int page, int size) {
        return commentDAO.findByEventId(eventId).stream().map(CommentDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO createEventComment(Long eventId, CommentDTO commentDTO) {
        commentDTO.setCreatedAt(LocalDateTime.now());
        commentDTO.setEventId(eventId);
        redisDAO.cacheEventStats(eventId, userEventTrackingDAO.getEventStats(eventId));
        return CommentDTO.fromEntity(commentDAO.insert(commentDTO.toEntity()));
    }

    @Transactional
    public List<EventDTO> getTopEvents() {
        List<EventDTO> cachedEvents = redisDAO.getTopViewedEvents(100).stream()
                .map(id -> eventDAO.findById(id))
                .map(EventDTO::fromEntity)
                .toList();

        if (cachedEvents.isEmpty()) {
            System.out.println("Cache MISS! TopViewedEvents");
            List<EventDTO> dbEvents = eventDAO.getTopViewedEvents(0, 100, LocalDateTime.now().minusDays(30)).stream().map(EventDTO::fromEntity).toList();
            dbEvents.forEach(event -> redisDAO.cacheEventStats(event.getId(), userEventTrackingDAO.getEventStats(event.getId())));
            return dbEvents;
        } else {
            System.out.println("Cache HIT! TopViewedEvents");
            return cachedEvents;
        }
    }

    public EventDTO getEvent(Long id) {
        return getEvent(id, currentUser.get().getId());
    }

    @Transactional
    public List<EventDTO> getMostReactedEvents() {
        List<EventDTO> cachedEvents = redisDAO.getTopReactedEvents(10).stream()
                .map(id -> eventDAO.findById(id))
                .map(EventDTO::fromEntity)
                .toList();

        if (cachedEvents.isEmpty()) {
            System.out.println("Cache MISS! TopReactedEvents");
            List<EventDTO> dbEvents = eventDAO.getTopReactedEvents(0, 100).stream().map(EventDTO::fromEntity).toList();
            dbEvents.forEach(event -> redisDAO.cacheEventStats(event.getId(), userEventTrackingDAO.getEventStats(event.getId())));
            return dbEvents;
        } else {
            System.out.println("Cache HIT! TopReactedEvents");
            return cachedEvents;
        }
    }

    public List<EventDTO> getSimiralEvents(Long eventId) {
        EventDTO event = getEvent(eventId, currentUser.get().getId());
        String tag = event.getTags().getFirst();
        return getEvents(0, 5, null, null, tag);
    }

    @Transactional
    public Boolean rsvpEvent(Long eventId, String email) {
        int count = rsvpDAO.getCount(eventId);
        Event ev = eventDAO.findById(eventId);
        if (ev.getMaxOccupants() > count) {
            RSVP rsvp = new RSVP();
            rsvp.setEvent(ev);
            rsvp.setCombinedKey(new RSVP.CombinedKey(email, eventId));
            rsvpDAO.insert(rsvp);
            return true;
        } else
            return false;
    }

    public String rsvpString(Long eventId) {
        int count = rsvpDAO.getCount(eventId);
        Event ev = eventDAO.findById(eventId);

        if (ev.getMaxOccupants() == 0)
            return "";
        return count + "/" + ev.getMaxOccupants();
    }

    @Transactional
    public String reactEvent(Long eventId, UserReactionDTO reaction) {
        UserEventStatusDTO status = userEventTrackingDAO.getUserEventStatus(currentUser.get().getId(), eventId);
        if (status.getReaction().getReaction() == UserReactionDTO.UserReactionDTOEnum.NO_VIEW)
            redisDAO.incrementView(eventId);
        if (status.getReaction().getReaction() == reaction.getReaction())
            return "";
        switch (status.getReaction().getReaction()) {
            case LIKE -> redisDAO.decrementLike(eventId);
            case DISLIKE -> redisDAO.decrementDislike(eventId);
        }
        switch (reaction.getReaction()) {
            case LIKE -> redisDAO.incrementLike(eventId);
            case DISLIKE -> redisDAO.incrementDislike(eventId);
        }

        userEventTrackingDAO.upsertReaction(eventId, currentUser.get().getId(), reaction.toUserReaction());
        return "";
    }

    public UserReaction upsertCommentReaction(Long commentId, UserReaction type) {
        return userCommentTrackingDAO.upsertCommentReaction(currentUser.get().getId(), commentId, type);
    }

    public CommentStats getCommentStats(Long commentId){
        return userCommentTrackingDAO.getCommentStats(commentId, currentUser.get().getId());
    }
}
