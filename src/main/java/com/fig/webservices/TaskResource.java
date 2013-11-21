package com.fig.webservices;

import com.fig.domain.ErrorResponse;
import com.fig.domain.SuccessResponse;
import com.fig.domain.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/20/13
 * Time: 1:13 AM
 */
@Path("/task")
public class TaskResource {
    private static final Logger LOG = LoggerFactory.getLogger(TaskResource.class);
    private static final Type TASKS = new TypeToken<Collection<Task>>(){}.getType();

    //TODO create homepage with link to all resources and sample

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/create")
    public Response create(@DefaultValue("[]") @FormParam("request") String request) {
        // Require both properties to create.
        Gson gson = new Gson();
        if (request == null) {
            String reason = "Property 'request' is missing.";
            String message = "Request to create task(s) failed !!!";
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(gson.toJson(new ErrorResponse(reason, message))). //TODO see if we could return the object directly instead of manually converting to json
                    build();
        }

        final Collection<Task> tasks = gson.fromJson(request, TASKS);
        LOG.debug("Tasks: {}", tasks);

        final SuccessResponse response = new SuccessResponse("Request accepted successfully. ");

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SuccessResponse.class, new SuccessResponse(""));
        gson = gsonBuilder.create();

        return Response.ok(gson.toJson(response)).build();
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
