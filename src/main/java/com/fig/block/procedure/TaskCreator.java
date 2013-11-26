package com.fig.block.procedure;

import com.fig.annotations.ThreadSafe;
import com.fig.domain.Task;
import com.fig.manager.Neo4jTaskAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.gs.collections.api.block.procedure.Procedure;

import java.util.Collection;

/**
 * Re-usable procedure to create tasks.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:33 PM
 */
@ThreadSafe
public class TaskCreator implements Procedure<Collection<Task>> {
    private final Neo4jTaskAdapter adapter = new Neo4jTaskAdapter();

    @Override
    public void value(Collection<Task> tasks) {
        // Nodes are created first before the dependencies to handle cases where a task name is referred on a
        // dependency before the task is created.
        for (Task task: tasks) {
            getAdapter().createTask(task);
        }

        //Followed by dependencies
        for (Task task : tasks) {
            getAdapter().createTaskDependencies(task);
        }
    }

    @VisibleForTesting
    Neo4jTaskAdapter getAdapter() {
        return adapter;
    }
}
