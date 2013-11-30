package com.fig.webservices;

import com.fig.domain.ErrorResponse;
import com.fig.domain.SuccessResponse;
import com.fig.domain.Task;
import com.fig.manager.TaskManager;
import com.fig.webservices.validators.TaskCreateRequestValidator;
import com.fig.webservices.validators.TaskUpdateRequestValidator;
import com.fig.domain.ValidationResponse;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.gs.collections.api.block.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

import static com.fig.util.BindingUtil.toJson;
import static com.google.common.base.Strings.isNullOrEmpty;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.ok;
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

    private TaskManager taskManager;
    private static final Splitter TASK_NAME_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
    private static final Function<String, ValidationResponse> TASK_CREATE_REQUEST_VALIDATOR = new TaskCreateRequestValidator();
    private static final Function<String, ValidationResponse> TASK_UPDATE_REQUEST_VALIDATOR = new TaskUpdateRequestValidator();

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/create")
    public Response create(@FormParam("request") String request) {
        final ValidationResponse response = TASK_CREATE_REQUEST_VALIDATOR.valueOf(request);

        if(response.getResponse().getStatusInfo().equals(OK)){
            final Task[] tasks = (Task[])response.getOutput();
            LOG.info("Request received to create {} tasks: {}", tasks.length);

            final SuccessResponse successResponse = response.getSuccessResponse();
            final String requestId = successResponse.getRequestId();
            getTaskManager().createTasks(Lists.newArrayList(tasks));
        }

        return response.getResponse();
    }

    /**
     *
     * @param taskNames Comma separated list of task names
     * @return
     */
    @GET
    @Produces({MediaType.TEXT_PLAIN}) //TODO return both json and plain text formats
    @Path("/query/{task}")
    public Response query(@PathParam("task") String taskNames) {
        //Parse the input
        Set<String> taskNameSet = parseTaskNames(taskNames);

        if(taskNameSet.isEmpty()){
            String reason = "No task name provided in the request";
            String message = "Unable to query for tasks";
            return status(BAD_REQUEST).
                    entity(toJson(new ErrorResponse(reason, message))). //TODO see if we could return the object directly instead of manually converting to json
                    build();
        }

        //Query the database
        final Set<Task> tasks = getTaskManager().getTasks(taskNameSet);

        if(tasks.isEmpty()){
            return ok().
                    entity(toJson(new SuccessResponse("Task not found in the database by name: " + taskNameSet))). //TODO see if we could return the object directly instead of manually converting to json
                    build();
        }

        return Response.ok(toJson(tasks)).build();
    }

    /**
     *
     * @param request JSON object with one of more tasks to update
     * @return
     */
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/update")
    public Response update(@FormParam("request") String request){
        final ValidationResponse response = TASK_UPDATE_REQUEST_VALIDATOR.valueOf(request);

        if(response.getResponse().getStatusInfo().equals(OK)){
            Task[] tasks = (Task[]) response.getOutput();
            LOG.info("Request received to update {} tasks.", tasks.length);
            getTaskManager().updateTaskProperties(Lists.newArrayList(tasks));
        }

        return response.getResponse();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/delete/{task}") //TODO fix the pattern
    public Response delete(@PathParam("task") String task) {
        return null;
    }

    public TaskManager getTaskManager() {
        if(this.taskManager == null){
            this.taskManager = new TaskManager();
        }
        return taskManager;
    }

    /**
     * Parses the given comma separated string of task names and returns them as Set<String>. It trims the individual
     * string and eliminates empty strings.
     * @param commaSeparatedTaskNames
     * @return
     */
    @VisibleForTesting
    Set<String> parseTaskNames(String commaSeparatedTaskNames){
        if(isNullOrEmpty(commaSeparatedTaskNames)){
            return Collections.emptySet();
        }

        return Sets.newHashSet(TASK_NAME_SPLITTER.split(commaSeparatedTaskNames));
    }
}
