package com.example.ispitni_projekat_f.api;

import com.example.ispitni_projekat_f.model.dto.CommentDTO;
import com.example.ispitni_projekat_f.model.dto.EventDTO;
import com.example.ispitni_projekat_f.model.dto.UserReactionDTO;
import com.example.ispitni_projekat_f.model.entity.UserReaction;
import com.example.ispitni_projekat_f.services.EventService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Event {

    @Inject
    private EventService eventService;

    @GET
    @PermitAll
    public Response listEvents(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return Response.status(Response.Status.OK).entity(eventService.getEvents(page, size)).build();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public Response getEvent(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(eventService.getEvent(id)).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "EVENT_CREATOR"})
    public Response createEvent(@Valid EventDTO event) {
        return Response.status(Response.Status.OK).entity(eventService.createEvent(event)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "EVENT_CREATOR"})
    public Response updateEvent(@PathParam("id") Long id, @Valid EventDTO event) {
        return Response.status(Response.Status.OK).entity(eventService.updateEvent(id, event)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "EVENT_CREATOR"})
    public Response deleteEvent(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(eventService.deleteEvent(id)).build();
    }

    @GET
    @PermitAll
    @Path("/search")
    public Response searchEvents(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size,
            @QueryParam("search") String search,
            @QueryParam("category") Long categoryId,
            @QueryParam("tag") String tag) {
        return Response.status(Response.Status.OK).entity(eventService.getEvents(page, size, search, categoryId, tag)).build();
    }

    @GET
    @PermitAll
    @Path("/top")
    public Response listMostViewed(@QueryParam("days") @DefaultValue("30") @Min(30) @Max(60) int lastNDays) {
        return Response.status(Response.Status.OK).entity(eventService.getTopEvents()).build();
    }

    @GET
    @PermitAll
    @Path("/most-reacted")
    public Response listMostReacted() {
        return Response.status(Response.Status.OK).entity(eventService.getMostReactedEvents()).build();
    }

    @GET
    @PermitAll
    @Path("/{id}/similar")
    public Response getSimilarEvents(@PathParam("id") Long eventId) {
        List<EventDTO> events = eventService.getSimiralEvents(eventId);
        System.out.println(events.size() + "HELLO");
        return Response.status(Response.Status.OK).entity(events).build();
    }

    @GET
    @PermitAll
    @Path("/{id}/rsvpString")
    public Response getRSVPBB(@PathParam("id") Long eventId) {
        return Response.status(Response.Status.OK).entity(eventService.rsvpString(eventId)).build();
    }

    @POST
    @PermitAll
    @Path("/{id}/rsvp")
    public Response rsvp(@PathParam("id") Long eventId, String email) {
        if (eventService.rsvpEvent(eventId, email))
            return Response.status(Response.Status.OK).build();
        else
            return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @PermitAll
    @Path("/{id}/react")
    public Response reactEvent(@PathParam("id") Long eventId, UserReactionDTO reaction) {
        return Response.status(Response.Status.OK).entity(eventService.reactEvent(eventId, reaction)).build();
    }

    @POST
    @PermitAll
    @Path("/comments/{id}/react")
    public Response reactComment(@PathParam("id") Long commentId, UserReaction reaction) {
        return Response.status(Response.Status.OK).entity(eventService.upsertCommentReaction(commentId, reaction)).build();
    }

    @GET
    @PermitAll
    @Path("/comments/{id}")
    public Response commentStats(@PathParam("id") Long commentId) {
        return Response.status(Response.Status.OK).entity(eventService.getCommentStats(commentId)).build();
    }

    @GET
    @PermitAll
    @Path("/{id}/comments")
    public Response listComments(
            @PathParam("id") Long eventId,
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return Response.status(Response.Status.OK).entity(eventService.getEventComments(eventId, page, size)).build();
    }

    @POST
    @PermitAll
    @Path("/{id}/comments")
    public Response addComment(@PathParam("id") Long eventId, @Valid CommentDTO comment) {
        return Response.status(Response.Status.OK).entity(eventService.createEventComment(eventId, comment)).build();
    }

    @POST
    @PermitAll
    @Path("/{eventId}/comments")
    public Response createComment(@PathParam("eventId") Long eventId, @Valid CommentDTO commentDTO) {
        return Response.status(Response.Status.OK).entity(eventService.createEventComment(eventId, commentDTO)).build();
    }
}
