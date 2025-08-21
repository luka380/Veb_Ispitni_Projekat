package com.example.ispitni_projekat_f.utils;

import jakarta.annotation.Priority;
import jakarta.ejb.EJBException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;

import java.net.ConnectException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.hibernate.LazyInitializationException;
import org.hibernate.exception.JDBCConnectionException;


@Provider
@Priority(Priorities.USER)
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable ex) {
        Throwable root = unwrap(ex);

        if (root instanceof WebApplicationException wae) {
            logAccordingToStatus(wae.getResponse().getStatus(), wae);
            return withPayload(wae.getResponse().getStatus(),
                    "Request failed",
                    message(wae),
                    uri(),
                    List.of());
        }

        if (root instanceof ConstraintViolationException cve) {
            List<String> details = new ArrayList<>();
            for (ConstraintViolation<?> v : cve.getConstraintViolations()) {
                details.add(v.getPropertyPath() + ": " + v.getMessage());
            }
            log4xx(cve);
            return withPayload(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Validation failed",
                    "One or more constraints were violated",
                    uri(),
                    details);
        }
        if (root instanceof ValidationException ve) {
            log4xx(ve);
            return withPayload(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Validation failed",
                    message(ve),
                    uri(),
                    List.of());
        }

        if (root instanceof JsonParseException || root instanceof JsonMappingException) {
            log4xx(root);
            return withPayload(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Malformed JSON",
                    "Request body could not be parsed or mapped",
                    uri(),
                    List.of(message(root)));
        }

        if (root instanceof NoResultException) {
            log4xx(root);
            return withPayload(Response.Status.NOT_FOUND.getStatusCode(),
                    "Not found",
                    message(root),
                    uri(),
                    List.of());
        }

        if (root instanceof jakarta.ws.rs.NotAuthorizedException) {
            log4xx(root);
            return withPayload(Response.Status.UNAUTHORIZED.getStatusCode(),
                    "Unauthorized",
                    message(root),
                    uri(),
                    List.of());
        }
        if (root instanceof jakarta.ws.rs.ForbiddenException) {
            log4xx(root);
            return withPayload(Response.Status.FORBIDDEN.getStatusCode(),
                    "Forbidden",
                    message(root),
                    uri(),
                    List.of());
        }

        if (root instanceof OptimisticLockException) {
            log4xx(root);
            return withPayload(Response.Status.CONFLICT.getStatusCode(),
                    "Update conflict",
                    "The resource was modified by another transaction",
                    uri(),
                    List.of());
        }

        if (root instanceof EntityExistsException || root instanceof SQLIntegrityConstraintViolationException) {
            log4xx(root);
            return withPayload(Response.Status.CONFLICT.getStatusCode(),
                    "Duplicate or constraint violation",
                    "The operation violated a unique or integrity constraint",
                    uri(),
                    List.of());
        }

        if (root instanceof JDBCConnectionException
                || root instanceof SQLTransientConnectionException
                || root instanceof SQLNonTransientConnectionException
                || root instanceof ConnectException) {
            log5xx(root);
            return withPayload(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
                    "Service unavailable",
                    "A downstream service or database is not reachable",
                    uri(),
                    List.of());
        }

        if (root instanceof LazyInitializationException) {
            log5xx(root);
            return withPayload(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    "Server error",
                    "Attempted to access data outside of an active persistence context",
                    uri(),
                    List.of());
        }

        if (root instanceof IllegalArgumentException) {
            log4xx(root);
            return withPayload(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Bad request",
                    message(root),
                    uri(),
                    List.of());
        }

        if (root instanceof PersistenceException pe) {
            Throwable cause = unwrap(pe.getCause());
            if (cause instanceof JDBCConnectionException
                    || cause instanceof SQLTransientConnectionException
                    || cause instanceof SQLNonTransientConnectionException) {
                log5xx(pe);
                return withPayload(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
                        "Service unavailable",
                        "A database connectivity error occurred",
                        uri(),
                        List.of());
            }
            log5xx(pe);
            return withPayload(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    "Persistence error",
                    message(pe),
                    uri(),
                    List.of());
        }

        log5xx(root);
        return withPayload(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Server error",
                "An unexpected error occurred",
                uri(),
                List.of(message(root)));
    }

    private String uri() {
        return uriInfo != null && uriInfo.getRequestUri() != null ? uriInfo.getRequestUri().toString() : "";
    }

    private static String message(Throwable t) {
        String msg = t.getMessage();
        return (msg == null || msg.isBlank()) ? t.getClass().getSimpleName() : msg;
    }

    private static Throwable unwrap(Throwable t) {
        if (t instanceof EJBException ejb && ejb.getCausedByException() != null) {
            return unwrap(ejb.getCausedByException());
        }
        Throwable cause = t.getCause();
        if (cause != null && cause != t) {
            return unwrap(cause);
        }
        return t;
    }

    private static void log4xx(Throwable t) {
        LOG.log(Level.WARNING, t.getClass().getSimpleName() + ": " + Objects.toString(t.getMessage(), ""), t);
    }

    private static void log5xx(Throwable t) {
        LOG.log(Level.SEVERE, t.getClass().getSimpleName() + ": " + Objects.toString(t.getMessage(), ""), t);
    }

    private static void logAccordingToStatus(int status, Throwable t) {
        if (status >= 500) log5xx(t); else log4xx(t);
    }

    private static Response withPayload(int status, String error, String message, String path, List<String> details) {
        ErrorPayload payload = new ErrorPayload(
                OffsetDateTime.now().toString(),
                status,
                error,
                message,
                path,
                details == null ? List.of() : details
        );
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(payload)
                .build();
    }

    public static class ErrorPayload {
        public String timestamp;
        public int status;
        public String error;
        public String message;
        public String path;
        public List<String> details;

        public ErrorPayload() {}

        public ErrorPayload(String timestamp, int status, String error, String message, String path, List<String> details) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.details = details;
        }
    }
}

