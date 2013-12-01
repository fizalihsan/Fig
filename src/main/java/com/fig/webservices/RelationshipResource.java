package com.fig.webservices;

import com.fig.domain.TaskDependency;
import com.fig.domain.ValidationResponse;
import com.fig.manager.TaskManager;
import com.fig.webservices.validators.TaskDependencyRequestValidator;
import com.gs.collections.api.block.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.OK;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/21/13
 * Time: 8:19 AM
 */
@Path("/relation")
public class RelationshipResource {
    private static final Logger LOG = LoggerFactory.getLogger(RelationshipResource.class);
    private TaskManager taskManager;
    private static final Function<String, ValidationResponse> TASK_DEPENDENCY_REQUEST_VALIDATOR = new TaskDependencyRequestValidator();

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/create")
    @SuppressWarnings("unchecked")
    public Response create(@FormParam("request") String request) {
        final ValidationResponse response = TASK_DEPENDENCY_REQUEST_VALIDATOR.valueOf(request);

        if(response.getResponse().getStatusInfo().equals(OK)){
            final List<TaskDependency> taskDependencies = (List<TaskDependency>)response.getOutput();
            getTaskManager().createTaskDependencies(taskDependencies);
        }
        return response.getResponse();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/delete")
    @SuppressWarnings("unchecked")
    public Response delete(@FormParam("request") String request) {
        final ValidationResponse response = TASK_DEPENDENCY_REQUEST_VALIDATOR.valueOf(request);

        if(response.getResponse().getStatusInfo().equals(OK)){
            final List<TaskDependency> taskDependencies = (List<TaskDependency>)response.getOutput();
            getTaskManager().deleteDependencies(taskDependencies);
        }
        return response.getResponse();
    }

    public TaskManager getTaskManager() {
        if(this.taskManager == null){
            this.taskManager = new TaskManager();
        }
        return taskManager;
    }
}
