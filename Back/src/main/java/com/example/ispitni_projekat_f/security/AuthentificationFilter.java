package com.example.ispitni_projekat_f.security;

import com.example.ispitni_projekat_f.model.dto.UserDTO;
import com.example.ispitni_projekat_f.model.entity.UserStatus;
import com.example.ispitni_projekat_f.model.entity.UserType;
import com.example.ispitni_projekat_f.services.UserService;
import com.example.ispitni_projekat_f.utils.CurrentUser;
import com.example.ispitni_projekat_f.utils.JwtUtils;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthentificationFilter implements ContainerRequestFilter {
    @Context
    private HttpServletRequest httpRequest;
    @Context
    private HttpServletResponse httpResponse;
    @Inject
    private UserService userService;
    @Inject
    private CurrentUser currentUser;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            return;
        }

        String jwtHeader = httpRequest.getHeader("Authorization");
        String jwtCookie = getCookieValue(httpRequest, "Authorization");
        String jsessionid = getCookieValue(httpRequest, "JSESSIONID");
        UserDTO userDTO = null;

        if (jsessionid == null) {
            userDTO = getNewUserAndCookie();
        }

        if (jwtCookie != null) {
            userDTO = processJWT(jwtCookie, requestContext);
        } else if (jwtHeader != null) {
            userDTO = processJWT(jwtHeader, requestContext);
        } else if (jsessionid != null) {
            try {
                int sessionId = Integer.parseInt(jsessionid);
                userDTO = userService.getUserById(sessionId);
                if (userDTO == null) {
                    userDTO = getNewUserAndCookie();
                }
            } catch (NumberFormatException nfe) {
                removeCookie("JSESSIONID");
                userDTO = getNewUserAndCookie();
            }
        }

        if (userDTO == null)
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());

        currentUser.set(userDTO);
        requestContext.setSecurityContext(new AppSecurityContext(userDTO, true, "Cookie"));
    }

    private void giveSessionCookie(UserDTO userDTO) {
        String value = Long.toString(userDTO.getId());
        addCookieHeader("JSESSIONID", value, 60 * 60 * 24 * 365, true, "Lax");
    }

    private void removeCookie(String name) {
        addCookieHeader(name, "", 0, true, "Lax");
    }

    private void addCookieHeader(String name, String value, int maxAgeSeconds, boolean httpOnly, String sameSite) {
        String safeValue = value == null ? "" : value;
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(safeValue)
                .append("; Path=/")
                .append("; Max-Age=").append(maxAgeSeconds)
                .append("; SameSite=").append(sameSite != null ? sameSite : "Lax");
        if (httpOnly) sb.append("; HttpOnly");
        httpResponse.addHeader("Set-Cookie", sb.toString());
    }

    private UserDTO processJWT(String authValue, ContainerRequestContext requestContext) {
        if (authValue == null || authValue.isEmpty()) return null;

        String jwt = authValue.startsWith("Bearer ") ? authValue.substring(7) : authValue;

        try {
            UserDTO user = JwtUtils.parseToken(jwt);
            user = userService.getUserById(user.getId());
            if (user == null) {
                removeCookie("Authorization");
            }
            return user;
        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("You are not authorized to access this resource.")
                            .build()
            );
            return null;
        }
    }

    private String getCookieValue(HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private UserDTO getNewUserAndCookie() {
        UserDTO user = userService.createUser(new UserDTO(
                0, null, null, null, null, UserType.ANONYMOUS, UserStatus.ACTIVE, null));
        giveSessionCookie(user);
        return user;
    }
}
