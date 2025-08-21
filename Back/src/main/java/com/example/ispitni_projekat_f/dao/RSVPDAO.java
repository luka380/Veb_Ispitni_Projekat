package com.example.ispitni_projekat_f.dao;

import com.example.ispitni_projekat_f.model.entity.RSVP;
import jakarta.ejb.Stateless;

@Stateless
public class RSVPDAO extends SimpleAbstractDAO<RSVP, RSVP.CombinedKey> {

    protected RSVPDAO() {
        super(RSVP.class);
    }

    public int getCount(Long eventId) {
        return em.createQuery("SELECT COUNT(r) FROM RSVP r WHERE r.combinedKey.eventId = :eventId", Long.class).setParameter("eventId", eventId).getSingleResult().intValue();
    }
}
