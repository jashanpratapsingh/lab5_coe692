package com.mycompany.lab5.checkoutpenalty.endpoint;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health")
public class MainEndpoint {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "checkout-penalty-service running";
    }
}
