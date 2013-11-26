package com.fig.webservices;

import com.fig.domain.ErrorResponse;
import com.fig.domain.SuccessResponse;
import com.fig.domain.Task;
import com.fig.manager.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

import static com.fig.util.BindingUtil.fromJsonArray;
import static com.fig.util.BindingUtil.toJson;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.status;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/20/13
 * Time: 1:13 AM
 */
@Path("/task")
public class TaskResource {
    private static final Logger LOG = LoggerFactory.getLogger(TaskResource.class);

    private TaskManager taskManager = new TaskManager();

    //TODO create homepage with link to all resources and sample

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/create")
    public Response create(@DefaultValue("[]") @FormParam("request") String request) {
        // Require both properties to create.
        if (request == null) {
            String reason = "Property 'request' is missing.";
            String message = "Request to create task(s) failed !!!";
            return status(BAD_REQUEST).
                    entity(toJson(new ErrorResponse(reason, message))). //TODO see if we could return the object directly instead of manually converting to json
                    build();
        }

        final Collection<Task> tasks = fromJsonArray(request, Task.class);
        LOG.debug("Tasks: {}", tasks);

        getTaskManager().createTasks(tasks);

        final SuccessResponse response = new SuccessResponse("Request accepted successfully. ");
        return Response.ok(toJson(response)).build();
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN}) //TODO return both json and plain text formats
    @Path("/query/{task}")
    public Response query(@PathParam("task") String taskName) {
        final Task task = getTaskManager().getTask(taskName);
        if(task==null){
            String reason = "Task name not found in database";
            String message = "Unable to query the given task";
            return status(BAD_REQUEST).
                    entity(toJson(new ErrorResponse(reason, message))). //TODO see if we could return the object directly instead of manually converting to json
                    build();
        } else {
            return Response.ok(toJson(task)).build();
        }
    }

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/update")
    public Response update(@FormParam("request") String request){
        // Require both properties to create.
        if (request == null) {
            String reason = "Property 'request' is missing.";
            String message = "Request to update task(s) failed !!!";
            return status(BAD_REQUEST).
                    entity(toJson(new ErrorResponse(reason, message))). //TODO see if we could return the object directly instead of manually converting to json
                    build();
        }

        final Collection<Task> tasks = fromJsonArray(request, Task.class);
        LOG.debug("Tasks: {}", tasks);

        getTaskManager().updateTaskProperties(tasks);

        return Response.ok(toJson(new SuccessResponse("Request accepted successfully. "))).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/delete/{task}") //TODO fix the pattern
    public Response delete(@PathParam("task") String task) {
        return null;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

}
