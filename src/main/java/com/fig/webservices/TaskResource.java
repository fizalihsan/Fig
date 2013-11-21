package com.fig.webservices;

import com.fig.domain.ErrorResponse;
import com.fig.domain.SuccessResponse;
import com.fig.domain.Task;
import com.google.gson.Gson;
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

    @GET
    @Produces({MediaType.TEXT_PLAIN})
//    @Path("/plain")
//    public String getPlain(@DefaultValue("Hello") @QueryParam("greeting") String greeting) {
    public String getPlain() {
        return "Hello World!!!";
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/create")
    public Response create(@DefaultValue("[]") @FormParam("request") String request) {
        // Require both properties to create.
        final Gson gson = new Gson();
        if (request == null) {
            String reason = "Property 'request' is missing.";
            String message = "Request to create task(s) failed !!!";
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(gson.toJson(new ErrorResponse(reason, message))). //TODO see if we could return the object directly instead of manually converting to json
                    build();
        }

        final Collection<Task> tasks = gson.fromJson(request, TASKS);
        System.out.println(tasks);

        final SuccessResponse response = new SuccessResponse("Request accepted successfully. ");
        return Response.ok(gson.toJson(response)).build();
    }
}
