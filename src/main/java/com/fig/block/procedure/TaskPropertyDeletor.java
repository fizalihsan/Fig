package com.fig.block.procedure;

import com.fig.annotations.ThreadSafe;
import com.fig.domain.Task;
import com.fig.util.Neo4jTaskAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.gs.collections.api.block.procedure.Procedure;
import org.neo4j.graphdb.Node;

import java.util.Collection;
import java.util.Map;

/**
 * Re-usable procedure to delete task properties.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:32 PM
 */
@ThreadSafe
public class TaskPropertyDeletor implements Procedure<Collection<Task>> {
    private final Neo4jTaskAdapter adapter = new Neo4jTaskAdapter();

    /**
     * Properties in the given task object will be removed from the database.
     * @param tasks
     */
    @Override
    public void value(Collection<Task> tasks) {
        for (Task task: tasks) {
            final Node node = getNode(task.getName());

            for (Map.Entry<String, Object> entry : task.getProperties().entrySet()) {
                final String key = entry.getKey();
                node.removeProperty(key);
            }
        }
    }

    @VisibleForTesting
    Node getNode(String taskName){
        return getAdapter().getNode(taskName);
    }

    @VisibleForTesting
    Neo4jTaskAdapter getAdapter() {
        return adapter;
    }
}