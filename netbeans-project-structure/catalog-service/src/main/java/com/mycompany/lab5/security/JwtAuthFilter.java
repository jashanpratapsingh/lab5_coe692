package com.mycompany.lab5.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.UriInfo;

import java.io.IOException;

@Provider
public class JwtAuthFilter implements ContainerRequestFilter {
    @Context
    private UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = uriInfo != null ? uriInfo.getPath(false) : "";
        if (path == null) path = "";
        // Jersey app path is configured as "api", but getPath(false) may include it.
        if (path.startsWith("api/")) path = path.substring("api/".length());

        if (path.equals("health") || path.endsWith("/health")) {
            return;
        }

        // Allow auth-service entry points even if this filter is registered.
        if (path.startsWith("auth/login") || path.startsWith("auth/validate")) {
            return;
        }

        String token = null;
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring("Bearer ".length()).trim();
        }
        if (token == null || token.isEmpty()) {
            token = JwtUtil.readTokenFromCookieHeader(requestContext.getHeaderString("Cookie"));
        }
        if (token == null || token.isEmpty()) {
            abort(requestContext);
            return;
        }
        if (!JwtUtil.validateToken(token)) {
            abort(requestContext);
            return;
        }

        requestContext.setProperty("authUsername", JwtUtil.getUsername(token));
    }

    private void abort(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"message\":\"Unauthorized\"}")
                        .build()
        );
    }
}

