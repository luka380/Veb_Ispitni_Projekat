package com.example.ispitni_projekat_f.api;

import com.example.ispitni_projekat_f.model.dto.UserDTO;
import com.example.ispitni_projekat_f.model.dto.UserLoginDTO;
import com.example.ispitni_projekat_f.services.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.time.Duration;


@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Authentification {

    @Inject
    private AuthService authService;

    @GET
    @PermitAll
    public Response getInfo() {
        UserDTO user = authService.getCurrentUser();
        user.setPassword(null);
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @POST
    @PermitAll
    @Path("/login")
    public Response login(@Valid UserLoginDTO userLoginDTO) {
        String jwt = authService.returnJWTLogin(userLoginDTO);
        if (jwt == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        int maxAgeSeconds = (int) Duration.ofDays(1).getSeconds();

        NewCookie jwtCookie = new NewCookie.Builder("Authorization")
                .value("Bearer " + jwt)
                .path("/")
                .sameSite(NewCookie.SameSite.LAX)
                .httpOnly(true)
                .maxAge(maxAgeSeconds)
                .build();

        return Response
                .ok()
                .header("Authorization", "Bearer " + jwt)
                .cookie(jwtCookie)
                .build();
    }


    @POST
    @RolesAllowed({"ADMIN", "EVENT_CREATOR"})
    @Path("/logout")
    public Response logout() {
        NewCookie jwtCookie = new NewCookie.Builder("Authorization")
                .value("")
                .path("/")
                .sameSite(NewCookie.SameSite.LAX)
                .httpOnly(true)
                .maxAge(0)
                .build();

        return Response
                .ok()
                .cookie(jwtCookie)
                .build();
    }
}
