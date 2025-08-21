package com.example.ispitni_projekat_f.api;

import com.example.ispitni_projekat_f.model.dto.CategoryDTO;
import com.example.ispitni_projekat_f.services.CategoryService;
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

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Category {

    @Inject
    private CategoryService categoryService;
    @Inject
    private EventService eventService;

    @GET
    @PermitAll
    public Response listCategories(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return Response.status(Response.Status.OK).entity(categoryService.getCategories(page, size)).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "EVENT_CREATOR"})
    public Response createCategory(@Valid CategoryDTO category) {
        return Response.status(Response.Status.OK).entity(categoryService.createCategory(category)).build();
    }

    @PUT
    @RolesAllowed({"ADMIN", "EVENT_CREATOR"})
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") Long id, CategoryDTO category) {
        return Response.status(Response.Status.OK).entity(categoryService.updateCategory(id, category)).build();
    }

    @DELETE
    @RolesAllowed({"ADMIN", "EVENT_CREATOR"})
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(categoryService.deleteCategory(id)).build();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public Response listCategoryEvents(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size,
            @PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(eventService.getEvents(page, size, null, id, null)).build();
    }
}
