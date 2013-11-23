package com.fig.manager;

import com.fig.annotations.Transactional;
import com.fig.domain.Task;
import com.fig.util.Neo4jUtil;
import com.google.common.annotations.VisibleForTesting;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/22/13
 * Time: 5:28 PM
 */
public class TaskManager {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManager.class);

    /**
     * For each task provided, this method does the following
     * <li> 1. Creates a node in the database. If a node by the same name already exists, an exception is thrown.</li>
     * <li> 2. For each node, the task properties are associated. </li>
     * <li> 3. Task dependencies are created. </li>
     * The entire processing is wrapped in a transaction.
     * @param taskCollection
     */
    @Transactional
    public void createTasks(Collection<Task> tasks){
        LOG.info("Creating {} tasks...", tasks.size());
        final Neo4jUtil util = getNeo4jUtil();
        final Transaction tx = util.beginTransaction();
        Task currentTask = null;
        try {

            // Nodes are created first before the dependencies to handle cases where a task name is referred on a
            // dependency before the task is created.
            for (Task task: tasks) {
                currentTask = task;
                util.createTask(task);
            }

            //Followed by dependencies
            for (Task task : tasks) {
                util.createTaskDependencies(task);
            }
            tx.success();
            LOG.info("Users created successfully...");
        } catch(Exception e) {
            throw new RuntimeException("Error creating task: " + currentTask, e);
        } finally {
            tx.finish();
        }
    }

    @VisibleForTesting
    Neo4jUtil getNeo4jUtil(){
        return Neo4jUtil.getInstance();
    }
}
