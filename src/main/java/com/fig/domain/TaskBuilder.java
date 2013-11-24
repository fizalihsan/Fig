package com.fig.domain;

import java.util.Map;
import java.util.Set;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/23/13
 * Time: 7:50 PM
 */
public class TaskBuilder {

    private Task task;
    private TaskBuilder(String name){
        this.task = new Task(name);
    }

    public static TaskBuilder task(String name){
        return new TaskBuilder(name);
    }

    public TaskBuilder dependsOn(Set<String> taskNames){
        this.task.setDependsOn(taskNames);
        return this;
    }

    public TaskBuilder properties(Map<String, Object> properties){
        this.task.setProperties(properties);
        return this;
    }

    public Task build(){
        if(this.task.getName()==null || this.task.getName().trim().length()==0){
            throw new RuntimeException("Task name is mandatory");
        }

        return task;
    }
}