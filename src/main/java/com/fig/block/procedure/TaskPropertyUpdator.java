package com.fig.block.procedure;

import com.fig.annotations.ThreadSafe;
import com.fig.domain.Task;
import com.fig.util.Neo4jTaskAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.gs.collections.api.block.procedure.Procedure;

import java.util.Collection;

/**
 * Re-usable procedure to update task properties.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:32 PM
 */
@ThreadSafe
public class TaskPropertyUpdator implements Procedure<Collection<Task>> {

    private final Neo4jTaskAdapter adapter = new Neo4jTaskAdapter();

    /**
     * Properties in the given task object will be updated in the database.
     * If the property does not exist, it will be created.
     * Properties not mentioned in the input task object will be untouched.
     * @param tasks
     */
    @Override
    public void value(Collection<Task> tasks) {
        for (Task task: tasks) {
            getAdapter().updateTaskProperties(task);
        }
    }

    @VisibleForTesting
    Neo4jTaskAdapter getAdapter() {
        return adapter;
    }
}