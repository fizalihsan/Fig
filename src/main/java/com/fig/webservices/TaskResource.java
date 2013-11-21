package com.fig.webservices;

import com.fig.domain.ErrorResponse;
import com.fig.domain.SuccessResponse;
import com.fig.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

import static com.fig.util.BindingUtil.fromJsonArray;
import static com.fig.util.BindingUtil.toJson;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/20/13
 * Time: 1:13 AM
 */
@Path("/task")
public class TaskResource {
    private static final Logger LOG = LoggerFactory.getLogger(TaskResource.class);

    //TODO create homepage with link to all resources and sample

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/create")
    public Response create(@DefaultValue("[]") @FormParam("request") String request) {
        // Require both properties to create.
        if (request == null) {
            String reason = "Property 'request' is missing.";
            String message = "Request to create task(s) failed !!!";
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(toJson(new ErrorResponse(reason, message))). //TODO see if we could return the object directly instead of manually converting to json
                    build();
        }

        final Collection<Task> tasks = fromJsonArray(request, Task.class);
        LOG.debug("Tasks: {}", tasks);

        final SuccessResponse response = new SuccessResponse("Request accepted successfully. ");
        return Response.ok(toJson(response)).build();
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN}) //TODO return both json and plain text formats
    @Path("/query/{task}") //TODO fix the pattern
    public String query(@PathParam("task") String task) {
        return "Querying Task: " + task;
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/update")
    public Response update(@FormParam("request") String request){
        return null;
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/delete/{task}") //TODO fix the pattern
    public Response delete(@PathParam("task") String task) {
        return null;
    }
}
