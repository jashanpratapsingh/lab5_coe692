package com.mycompany.lab5.reservation.endpoint;

import com.mycompany.lab5.reservation.business.ReservationManager;
import com.mycompany.lab5.reservation.helper.Reservation;
import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/reservations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReservationEndpoint {
    private final ReservationManager manager = new ReservationManager();

    @POST
    public Response create(Reservation reservation) {
        return Response.ok(manager.create(reservation)).build();
    }

    @GET
    public List<Reservation> all() { return manager.all(); }

    @PUT
    @Path("/{id}/cancel")
    public Response cancel(@PathParam("id") int id) {
        Reservation reservation = manager.cancel(id);
        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"Reservation not found\"}").build();
        }
        return Response.ok(reservation).build();
    }
}
