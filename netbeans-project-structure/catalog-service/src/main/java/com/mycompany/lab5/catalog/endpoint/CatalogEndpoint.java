package com.mycompany.lab5.catalog.endpoint;

import com.mycompany.lab5.catalog.business.CatalogManager;
import com.mycompany.lab5.catalog.helper.Equipment;
import java.util.List;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/catalog")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogEndpoint {
    private final CatalogManager manager = new CatalogManager();

    @GET
    @Path("/equipment")
    public List<Equipment> equipment(@QueryParam("query") String query) {
        return manager.list(query);
    }
}
