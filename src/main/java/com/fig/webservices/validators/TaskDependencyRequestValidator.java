package com.fig.webservices.validators;

import com.fig.annotations.ThreadSafe;
import com.fig.domain.TaskDependency;
import com.fig.domain.ValidationResponse;
import com.fig.exception.JsonSyntaxException;
import com.google.common.collect.Lists;
import com.gs.collections.api.block.function.Function;

import static com.fig.domain.ValidationResponse.Builder.response;
import static com.fig.util.BindingUtil.fromJsonArray;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Common function to validate the task dependency create/delete request
 * User: Fizal
 * Date: 11/28/13
 * Time: 7:23 PM
 */
@ThreadSafe
public class TaskDependencyRequestValidator implements Function<String, ValidationResponse> {

    @Override
    public ValidationResponse valueOf(String request) {
        if (isNullOrEmpty(request)) {
            return response().error("Property 'request' is missing or empty.", "Request to create/delete task(s) failed !!!").build();
        } else {
            try{
                TaskDependency[] taskDependencies = fromJsonArray(request, TaskDependency[].class);

                return response().success("Request accepted successfully. ").output(Lists.newArrayList(taskDependencies)).build();
            } catch (JsonSyntaxException e){
                return response().error(e.toString(), "Invalid JSON sent in request").build();
            }
        }
    }

}
