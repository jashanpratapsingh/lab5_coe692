package com.mycompany.lab5.inventory.endpoint;

import com.mycompany.lab5.inventory.business.InventoryManager;
import com.mycompany.lab5.inventory.helper.InventoryItem;
import java.util.Collection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryEndpoint {
    private final InventoryManager manager = new InventoryManager();

    @GET
    @Path("/availability")
    public Collection<InventoryItem> availability() { return manager.all(); }

    @PUT
    @Path("/item/{assetTag}/status")
    public Response update(@PathParam("assetTag") String assetTag, @QueryParam("status") String status) {
        InventoryItem item = manager.updateStatus(assetTag, status);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"Asset not found\"}").build();
        }
        return Response.ok(item).build();
    }
}
