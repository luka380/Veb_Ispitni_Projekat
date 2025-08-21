package com.example.ispitni_projekat_f.api;

import com.example.ispitni_projekat_f.model.dto.UserDTO;
import com.example.ispitni_projekat_f.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class User {

    @Inject
    private UserService userService;

    @GET
    @RolesAllowed("ADMIN")
    public Response listUsers(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return Response.status(Response.Status.OK).entity(userService.getUsers(page, size)).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    public Response createUser(@Valid UserDTO user) {
        return Response.status(Response.Status.OK).entity(userService.createUser(user)).build();
    }

    @PUT
    @RolesAllowed("ADMIN")
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, @Valid UserDTO user) {
        return Response.status(Response.Status.OK).entity(userService.updateUser(id, user)).build();
    }

    @DELETE
    @RolesAllowed("ADMIN")
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(userService.deleteUser(id)).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Path("/{id}/activate")
    public Response activateUser(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(userService.activateUser(id)).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Path("/{id}/deactivate")
    public Response deactivateUser(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(userService.deactivateUser(id)).build();
    }
}
