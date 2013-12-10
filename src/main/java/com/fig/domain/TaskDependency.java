package com.fig.domain;

import com.fig.annotations.Immutable;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * POJO representing the task dependencies
 * User: Fizal
 * Date: 11/30/13
 * Time: 2:02 PM
 */
@Immutable
public class TaskDependency implements Serializable{
    private String fromTask;
    private List<String> toTasks;

    private static final long serialVersionUID = 1L;

    public TaskDependency(String fromTask, List<String> toTasks) {
        this.fromTask = fromTask;
        this.toTasks = toTasks;
    }

    public String getFromTask() {
        return fromTask;
    }

    public List<String> getToTasks() {
        return toTasks==null?null:Lists.newArrayList(toTasks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskDependency that = (TaskDependency) o;

        if (!fromTask.equals(that.fromTask)) return false;
        if (!toTasks.equals(that.toTasks)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromTask.hashCode();
        result = 31 * result + toTasks.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskDependency{");
        sb.append("fromTask='").append(fromTask).append('\'');
        sb.append(", toTasks=").append(toTasks);
        sb.append('}');
        return sb.toString();
    }
}
