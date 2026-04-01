package com.mycompany.lab5;

import java.util.Set;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("api")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(com.mycompany.lab5.auth.endpoint.MainEndpoint.class);
        resources.add(com.mycompany.lab5.auth.endpoint.AuthEndpoint.class);
        resources.add(com.mycompany.lab5.security.JwtAuthFilter.class);
        return resources;
    }
}
