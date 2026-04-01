package com.mycompany.lab5.auth.endpoint;

import com.mycompany.lab5.auth.business.AuthManager;
import com.mycompany.lab5.auth.helper.LoginRequest;
import com.mycompany.lab5.auth.helper.LoginResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthEndpoint {
    private final AuthManager manager = new AuthManager();

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        LoginResponse response = manager.login(request.getUsername(), request.getPassword());
        return Response.ok(response).build();
    }

    @POST
    @Path("/validate")
    public Response validate(String token) {
        boolean valid = manager.validateToken(token);
        return Response.ok("{\"valid\":" + valid + "}").build();
    }
}
