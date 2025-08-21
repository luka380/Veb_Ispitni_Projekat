package com.example.ispitni_projekat_f.security;

import com.example.ispitni_projekat_f.model.dto.UserDTO;
import com.example.ispitni_projekat_f.model.entity.UserType;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

public record AppSecurityContext(UserDTO user, boolean secure, String scheme) implements SecurityContext {

    @Override
    public Principal getUserPrincipal() {
        return user == null ? null : () -> user.getName() == null ? user.getFirstName() + " " + user.getLastName() : user.getName();
    }

    @Override
    public boolean isUserInRole(String role) {
        if (user == null || role == null) return false;
        String r = role.trim();
        return user.getUserType() == UserType.valueOf(r.toUpperCase());
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return scheme;
    }
}
