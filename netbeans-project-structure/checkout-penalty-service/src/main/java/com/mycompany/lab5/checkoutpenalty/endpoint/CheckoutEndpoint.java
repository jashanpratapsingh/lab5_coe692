package com.mycompany.lab5.checkoutpenalty.endpoint;

import com.mycompany.lab5.checkoutpenalty.business.RentalManager;
import com.mycompany.lab5.checkoutpenalty.helper.RentalRecord;
import java.util.List;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/rentals")
@Produces(MediaType.APPLICATION_JSON)
public class CheckoutEndpoint {
    private final RentalManager manager = new RentalManager();

    @POST
    @Path("/checkout")
    public Response checkout(@QueryParam("username") String username, @QueryParam("assetTag") String assetTag, @QueryParam("dueDate") String dueDate) {
        return Response.ok(manager.checkout(username, assetTag, dueDate)).build();
    }

    @POST
    @Path("/return")
    public Response processReturn(@QueryParam("rentalId") int rentalId, @QueryParam("returnDate") String returnDate) {
        RentalRecord record = manager.processReturn(rentalId, returnDate);
        if (record == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"Rental not found\"}").build();
        }
        return Response.ok(record).build();
    }

    @GET
    @Path("/history/{userId}")
    public List<RentalRecord> history(@PathParam("userId") String userId) {
        return manager.history(userId);
    }
}
