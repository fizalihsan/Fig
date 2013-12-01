package com.fig.domain;

import com.fig.annotations.Immutable;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/23/13
 * Time: 7:50 PM
 */
@Immutable
public class TaskBuilder {

    private Task task;
    private TaskBuilder(String name){
        this.task = new Task(name);
    }

    public static TaskBuilder task(String name){
        return new TaskBuilder(name);
    }

    public TaskBuilder dependsOn(Collection<String> taskNames){
        this.task.setDependsOn(taskNames);
        return this;
    }

    public TaskBuilder properties(Map<String, Object> properties){
        this.task.setProperties(properties);
        return this;
    }

    public Task build(){
        checkNotNull(this.task.getName());
        checkArgument(this.task.getName().length()>0);
        return task;
    }
}