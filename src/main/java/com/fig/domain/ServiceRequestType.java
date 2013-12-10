package com.fig.domain;

import com.fig.manager.TaskManager;
import com.google.common.collect.Lists;
import com.gs.collections.api.block.procedure.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Each REST service request is denoted here as an enum with a corresponding request processor implementation.
 * User: Fizal
 * Date: 12/9/13
 * Time: 11:00 PM
 */
public enum ServiceRequestType {
    CREATE_TASK(new CreateTaskRequestProcessor()),
    UPDATE_TASK(new UpdateTaskRequestProcessor()),
    DELETE_TASK(new DeleteTaskRequestProcessor()),
    CREATE_DEPENDENCY(new CreateDependenyRequestProcessor()),
    DELETE_DEPENDENCY(new DeleteDependenyRequestProcessor());

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRequestType.class);

    ServiceRequestType(Procedure<ServiceRequest> processor){
        this.processor = processor;
    }

    private static TaskManager taskManager = new TaskManager();
    private Procedure<ServiceRequest> processor;

    public void process(ServiceRequest input){
        processor.value(input);
    }

    /**
     * Create Tasks
     */
    private static class CreateTaskRequestProcessor implements Procedure<ServiceRequest> {
        @Override
        public void value(ServiceRequest serviceRequest) {
            final ValidationResponse response = serviceRequest.getValidationResponse();
            final Task[] tasks = (Task[]) response.getOutput();
            LOG.info("Request received to create {} tasks: {}", tasks.length);

            final SuccessResponse successResponse = response.getSuccessResponse();
            final String requestId = successResponse.getRequestId();
            try {
                getTaskManager().createTasks(Lists.newArrayList(tasks));
            } catch (Exception e) {
                //TODO handle error case
                e.printStackTrace();
            }
        }
    }

    /**
     * Update Tasks
     */
    private static class UpdateTaskRequestProcessor implements Procedure<ServiceRequest> {
        @Override
        public void value(ServiceRequest serviceRequest) {
            final ValidationResponse response = serviceRequest.getValidationResponse();

            try {
                Task[] tasks = (Task[]) response.getOutput();
                LOG.info("Request received to update {} tasks.", tasks.length);
                getTaskManager().updateTaskProperties(Lists.newArrayList(tasks));
            } catch (Exception e) {
                //TODO handle error case
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete Tasks
     */
    private static class DeleteTaskRequestProcessor implements Procedure<ServiceRequest> {
        @Override
        public void value(ServiceRequest serviceRequest) {
            final ValidationResponse response = serviceRequest.getValidationResponse();

            try {
//                getTaskManager().deleteTasks();
            } catch (Exception e) {
                //TODO handle error case
                e.printStackTrace();
            }
        }
    }

    /**
     * Create Dependencies
     */
    private static class CreateDependenyRequestProcessor implements Procedure<ServiceRequest> {
        @Override
        public void value(ServiceRequest serviceRequest) {
            final ValidationResponse response = serviceRequest.getValidationResponse();

            try {
                final List<TaskDependency> taskDependencies = (List<TaskDependency>)response.getOutput();
                getTaskManager().createTaskDependencies(taskDependencies);
            } catch (Exception e) {
                //TODO handle error case
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete dependencies
     */
    private static class DeleteDependenyRequestProcessor implements Procedure<ServiceRequest> {
        @Override
        public void value(ServiceRequest serviceRequest) {
            final ValidationResponse response = serviceRequest.getValidationResponse();

            try {
                final List<TaskDependency> taskDependencies = (List<TaskDependency>)response.getOutput();
                getTaskManager().deleteDependencies(taskDependencies);
            } catch (Exception e) {
                //TODO handle error case
                e.printStackTrace();
            }
        }
    }

    static TaskManager getTaskManager() {
        return taskManager;
    }
}
