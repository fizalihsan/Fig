package com.fig.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/21/13
 * Time: 8:19 AM
 */
@Path("/relation")
public class RelationshipResource {
    private static final Logger LOG = LoggerFactory.getLogger(RelationshipResource.class);

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/create")
    public Response create(@DefaultValue("[]") @FormParam("request") String request) {
        return null;
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/delete}")
    public Response delete(@PathParam("request") String request) {
        return null;
    }
}
