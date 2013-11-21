package com.fig.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/21/13
 * Time: 8:27 AM
 */
@Path("/status")
public class StatusResource {
    private static final Logger LOG = LoggerFactory.getLogger(StatusResource.class);

    @GET
    @Produces({MediaType.TEXT_PLAIN}) //TODO return both json and plain text formats
    @Path("/request/{ids}") //TODO fix the pattern
    public String query(@PathParam("ids") String requestIds) {
        return "Requesting status for request ids: " + requestIds;
    }
}
