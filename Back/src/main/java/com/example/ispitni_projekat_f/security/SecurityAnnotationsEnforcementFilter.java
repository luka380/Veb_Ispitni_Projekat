package com.example.ispitni_projekat_f.security;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class SecurityAnnotationsEnforcementFilter implements ContainerRequestFilter {

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();
        Class<?> resourceClass = resourceInfo.getResourceClass();

        if (isAnnotatedWith(method, PermitAll.class)) {
            return;
        }
        if (isAnnotatedWith(method, DenyAll.class)) {
            throw new ForbiddenException("Access denied");
        }
        RolesAllowed ra = method.getAnnotation(RolesAllowed.class);
        if (ra != null) {
            enforceRoles(ra.value(), requestContext.getSecurityContext());
            return;
        }

        if (isAnnotatedWith(resourceClass, PermitAll.class)) {
            return; // allow
        }
        if (isAnnotatedWith(resourceClass, DenyAll.class)) {
            throw new ForbiddenException("Access denied");
        }
        ra = resourceClass.getAnnotation(RolesAllowed.class);
        if (ra != null) {
            enforceRoles(ra.value(), requestContext.getSecurityContext());
            return;
        }
    }

    private static boolean isAnnotatedWith(AnnotatedElement element, Class<?> ann) {
        return element != null && element.isAnnotationPresent((Class) ann);
    }

    private static void enforceRoles(String[] allowed, SecurityContext sc) {
        if (sc == null || sc.getUserPrincipal() == null) {
            throw new NotAuthorizedException("Bearer");
        }
        Set<String> required = new HashSet<>(Arrays.asList(allowed));
        boolean ok = required.stream().anyMatch(sc::isUserInRole);
        if (!ok) {
            throw new ForbiddenException("Insufficient role");
        }
    }
}
