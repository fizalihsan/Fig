package com.fig.block.procedure;

import com.fig.annotations.ThreadSafe;
import com.fig.domain.TaskDependency;
import com.fig.manager.Neo4jTaskAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.gs.collections.api.block.procedure.Procedure;

import java.util.Collection;

/**
 * Re-usable procedure to create task dependencies.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:26 PM
 */
@ThreadSafe
public class TaskDependencyCreator implements Procedure<Collection<TaskDependency>> {
    private final Neo4jTaskAdapter adapter = new Neo4jTaskAdapter();

    @Override
    public void value(Collection<TaskDependency> taskDependencies) {
        for (TaskDependency dependency : taskDependencies) {
            for (String toTask : dependency.getToTasks()) {
                getAdapter().createTaskDependency(dependency.getFromTask(), toTask);
            }
        }
    }

    @VisibleForTesting
    Neo4jTaskAdapter getAdapter() {
        return adapter;
    }
}
