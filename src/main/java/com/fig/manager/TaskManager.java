package com.fig.manager;

import com.fig.annotations.Transactional;
import com.fig.domain.Task;
import com.fig.util.Neo4jUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.gs.collections.api.tuple.Pair;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * Helper class to bridge the gap between business logic and Neo4j.
 *
 * Methods annotated with @Transactional creates its own transaction automatically.
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
     * @param tasks
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
            LOG.info("{} Tasks created successfully...", tasks.size());
        } catch(Exception e) {
            throw new RuntimeException("Error creating task: " + currentTask, e);
        } finally {
            tx.finish();
        }
    }

    /**
     * Queries the graph database for the given task names. If none are found, an empty collection is returned.
     * @param taskNames
     * @return
     */
    public Set<Task> getTasks(Set<String> taskNames){
        Set<Task> tasks = Sets.newHashSet();
        for (String taskName : taskNames) {
            final Task task = getNeo4jUtil().getTask(taskName);
            if(task!=null){
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Properties in the given tasks are updated. Dependencies will not be updated.
     * @param tasks
     */
    @Transactional
    public void updateTaskProperties(Collection<Task> tasks){
        LOG.info("Updating {} tasks...", tasks.size());
        final Neo4jUtil util = getNeo4jUtil();
        final Transaction tx = util.beginTransaction();
        Task currentTask = null;
        try {
            for (Task task: tasks) {
                currentTask = task;
                util.updateTaskProperties(task);
            }

            tx.success();
            LOG.info("{} Tasks updated successfully...", tasks.size());
        } catch(Exception e) {
            throw new RuntimeException("Error updating task: " + currentTask, e);
        } finally {
            tx.finish();
        }
    }

    /**
     * Deletes the given set of tasks. Throws exception if at least one of them couldn't be deleted.
     * @param taskNames
     */
    @Transactional
    public void deleteTasks(Set<String> taskNames){
        final Neo4jUtil util = getNeo4jUtil();
        final Transaction tx = util.beginTransaction();
        String currentTask = "";
        try {
            for (String taskName : taskNames) {
                currentTask = taskName;
                util.deleteTask(taskName);
            }
            tx.success();
            LOG.info("{} Tasks deleted successfully...", taskNames.size());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting task: " + currentTask, e);
        } finally {
            tx.finish();
        }
    }

    /**
     * Creates dependencies between the given pair of tasks
     * @param taskNamePairs
     */
    @Transactional
    public void createTaskDependencies(Set<Pair<String, String>> taskNamePairs){
        final Neo4jUtil util = getNeo4jUtil();
        final Transaction tx = util.beginTransaction();
        String currentFromTask = "", currentToTask = "";

        try {
            for (Pair<String, String> taskNamePair : taskNamePairs) {
                final String fromTask = taskNamePair.getOne();
                final String toTask = taskNamePair.getTwo();
                currentFromTask = fromTask;
                currentToTask = toTask;

                util.createTaskDependency(fromTask, toTask);
            }

            tx.success();
            LOG.info("{} task dependencies created successfully...", taskNamePairs.size());
        } catch (Exception e) {
            throw new RuntimeException("Error creating dependency between tasks: " + currentFromTask + " -> " + currentToTask, e);
        } finally {
            tx.finish();
        }
    }

    /**
     * Deletes dependencies between the given pair of tasks
     * @param taskNamePairs
     */
    @Transactional
    public void deleteDependencies(Set<Pair<String, String>> taskNamePairs){
        final Neo4jUtil util = getNeo4jUtil();
        final Transaction tx = util.beginTransaction();
        String currentFromTask = "", currentToTask = "";

        try {
            for (Pair<String, String> taskNamePair : taskNamePairs) {
                final String fromTask = taskNamePair.getOne();
                final String toTask = taskNamePair.getTwo();
                currentFromTask = fromTask;
                currentToTask = toTask;

                final boolean dependencyDeleted = util.deleteTaskDependency(fromTask, toTask);
                if(!dependencyDeleted){
                    LOG.info("Dependency from {} -> {} either not found or not deleted !!! ", fromTask, toTask);
                }
            }

            tx.success();
            LOG.info("Task dependency deletion completed ...");
        } catch (Exception e) {
            throw new RuntimeException("Error deleting dependency between tasks: " + currentFromTask + " -> " + currentToTask, e);
        } finally {
            tx.finish();
        }
    }

    @VisibleForTesting
    Neo4jUtil getNeo4jUtil(){
        return Neo4jUtil.getInstance();
    }
}
