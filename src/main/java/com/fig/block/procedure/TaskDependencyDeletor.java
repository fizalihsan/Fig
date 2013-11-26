package com.fig.block.procedure;

import com.fig.manager.Neo4jTaskAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Re-usable procedure to delete task dependencies.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:29 PM
 */
public class TaskDependencyDeletor implements Procedure<Collection<Pair<String, String>>> {
    private static final Logger LOG = LoggerFactory.getLogger(TaskDependencyDeletor.class);

    private final Neo4jTaskAdapter adapter = new Neo4jTaskAdapter();

    @Override
    public void value(Collection<Pair<String, String>> taskNamePairs) {
        for (Pair<String, String> taskNamePair : taskNamePairs) {
            final String fromTask = taskNamePair.getOne();
            final String toTask = taskNamePair.getTwo();

            final boolean dependencyDeleted = getAdapter().deleteTaskDependency(fromTask, toTask);
            if (!dependencyDeleted) {
                LOG.warn("Dependency from {} -> {} either not found or not deleted !!! ", fromTask, toTask);
            }
        }
    }

    @VisibleForTesting
    Neo4jTaskAdapter getAdapter() {
        return adapter;
    }
}