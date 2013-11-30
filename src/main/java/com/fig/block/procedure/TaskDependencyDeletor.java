package com.fig.block.procedure;

import com.fig.domain.TaskDependency;
import com.fig.manager.Neo4jTaskAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.gs.collections.api.block.procedure.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Re-usable procedure to delete task dependencies.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:29 PM
 */
public class TaskDependencyDeletor implements Procedure<Collection<TaskDependency>> {
    private static final Logger LOG = LoggerFactory.getLogger(TaskDependencyDeletor.class);

    private final Neo4jTaskAdapter adapter = new Neo4jTaskAdapter();

    @Override
    public void value(Collection<TaskDependency> taskNamePairs) {
        for (TaskDependency taskDependency : taskNamePairs) {
            final String fromTask = taskDependency.getFromTask();

            for (String toTask : taskDependency.getToTasks()) {
                final boolean dependencyDeleted = getAdapter().deleteTaskDependency(fromTask, toTask);
                if (!dependencyDeleted) {
                    LOG.warn("Dependency from {} -> {} either not found or not deleted !!! ", fromTask, toTask);
                }
            }
        }
    }

    @VisibleForTesting
    Neo4jTaskAdapter getAdapter() {
        return adapter;
    }
}