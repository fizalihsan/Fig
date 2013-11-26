package com.fig.manager;

import com.fig.annotations.Transactional;
import com.fig.block.procedure.*;
import com.fig.domain.Task;
import com.google.common.annotations.VisibleForTesting;
import com.gs.collections.api.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * This class exposes all the high-level business logic methods.
 *
 * Methods annotated with @Transactional creates its own transaction automatically.
 * User: Fizal
 * Date: 11/22/13
 * Time: 5:28 PM
 */
public class TaskManager {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManager.class);

    private Neo4jTaskAdapter adapter;

    private static final TransactionWrapper<Collection<Task>> TASK_CREATOR = new TransactionWrapper<>(new TaskCreator());
    private static final TransactionWrapper<Collection<Task>> TASK_PROPERTY_UPDATOR = new TransactionWrapper<>(new TaskPropertyUpdator());
    private static final TransactionWrapper<Collection<String>> TASK_DELETOR = new TransactionWrapper<>(new TaskDeletor());
    private static final TransactionWrapper<Collection<Pair<String, String>>> TASK_DEPENDENCY_CREATOR = new TransactionWrapper<>(new TaskDependencyCreator());
    private static final TransactionWrapper<Collection<Pair<String, String>>> TASK_DEPENDENCY_DELETOR = new TransactionWrapper<>(new TaskDependencyDeletor());

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
        TASK_CREATOR.value(tasks);
        LOG.info("{} Tasks created successfully...", tasks.size());
    }

    public Task getTask(String taskName){
        return getAdapter().getTask(taskName);
    }

    /**
     * Queries the graph database for the given task names. If none are found, an empty collection is returned.
     * @param taskNames
     * @return
     */
    public Set<Task> getTasks(Set<String> taskNames){
        Set<Task> tasks = newHashSet();
        for (String taskName : taskNames) {
            final Task task = getTask(taskName);
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
        TASK_PROPERTY_UPDATOR.value(tasks);
        LOG.info("{} Tasks updated successfully...", tasks.size());
    }

    /**
     * Deletes the given set of tasks. Throws exception if at least one of them couldn't be deleted.
     * @param taskNames
     */
    @Transactional
    public void deleteTasks(Set<String> taskNames){
        LOG.info("Deleting {} tasks: ", taskNames.size(), taskNames);
        TASK_DELETOR.value(taskNames);
        LOG.info("{} Tasks deleted successfully...", taskNames.size());
    }

    /**
     * Creates dependencies between the given pair of tasks
     * @param taskNamePairs
     */
    @Transactional
    public void createTaskDependencies(Collection<Pair<String, String>> taskNamePairs){
        LOG.info("Creating task dependencies for the following {} pairs: {}", taskNamePairs.size(), taskNamePairs);
        TASK_DEPENDENCY_CREATOR.value(taskNamePairs);
        LOG.info("{} task dependencies created successfully...", taskNamePairs.size());
    }

    /**
     * Deletes dependencies between the given pair of tasks
     * @param taskNamePairs
     */
    @Transactional
    public void deleteDependencies(Collection<Pair<String, String>> taskNamePairs){
        LOG.info("Deleting task dependencies for the following {} pairs: {}", taskNamePairs.size(), taskNamePairs);
        TASK_DEPENDENCY_DELETOR.value(taskNamePairs);
        LOG.info("{} task dependencies deleted successfully...", taskNamePairs.size());
    }

    @VisibleForTesting
    Neo4jTaskAdapter getAdapter(){
        if(this.adapter == null){
            this.adapter = new Neo4jTaskAdapter();
        }
        return this.adapter;
    }

}
