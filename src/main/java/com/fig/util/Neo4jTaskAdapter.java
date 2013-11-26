package com.fig.util;

import com.fig.domain.Task;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.fig.domain.TaskBuilder.task;
import static com.fig.domain.TaskRelations.DEPENDS_ON;
import static com.fig.util.Neo4jUtil.TASK_NAME;
import static org.neo4j.graphdb.Direction.OUTGOING;

/**
 * Adapter class between the Neo4jUtil and the rest of the application. It restricts the leakage of Neo4j related
 * classes to rest of the module.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:54 PM
 */
public class Neo4jTaskAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jTaskAdapter.class);

    //TODO annotate all the methods that require a transaction with EJB style annotations
    /**
     * Add the given task as a node in the database
     * @param task
     */
    public void createTask(Task task){
        if(doesTaskExistInDb(task)){
            throw new RuntimeException("Task '" + task.getName() + "' already exists !!! Duplicate tasks not allowed." );
        }

        Node node = getGraphDb().createNode();
        node.setProperty(TASK_NAME, task.getName() );
        getNodeNameIndex().add(node, TASK_NAME, task.getName());

        final Map<String, Object> properties = task.getProperties();
        if(properties!=null){
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                node.setProperty(entry.getKey(), entry.getValue() );
            }
        }
    }

    /**
     * Update the properties in the given task
     * @param task
     */
    public void updateTaskProperties(Task task){
        final Node node = getNode(task.getName());

        for (Map.Entry<String, Object> entry : task.getProperties().entrySet()) {
            node.setProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Delete the task properties mentioned in the input object
     * @param task
     */
    public void deleteTaskProperties(Task task){
        final Node node = getNode(task.getName());

        for (Map.Entry<String, Object> entry : task.getProperties().entrySet()) {
            final String key = entry.getKey();
            if(!key.equals(TASK_NAME)){ //Task name property should not be deleted.
                node.removeProperty(key);
            }
        }
    }

    /**
     * Delete the given task
     * @param taskName
     */
    public void deleteTask(String taskName){
        final Node node = getNode(taskName);

        final Iterable<Relationship> relationships = node.getRelationships();
        //TODO complete the delete logic
    }

    /**
     * Return true if the given task already existing in the database.
     * @param task
     * @return
     */
    public boolean doesTaskExistInDb(Task task){
        return getNodeNameIndex().get(TASK_NAME, task.getName()).hasNext();
    }

    /**
     * Creates dependencies from the given task to all the tasks defined within.
     * @param task
     */
    public void createTaskDependencies(Task task){
        final Set<String> dependsOn = task.getDependsOn();

        if(dependsOn==null){  return; }

        final Node node = getNode(task.getName());
        for (String targetNodeName : dependsOn) {
            final Node targetNode = getNode(targetNodeName);
            node.createRelationshipTo(targetNode, DEPENDS_ON);
        }
    }

    /**
     * Create a dependency between the two given tasks. Throws an exception even if one of the task does not exist.
     * @param fromTask
     * @param toTask
     */
    public void createTaskDependency(String fromTask, String toTask){
        final Node fromNode = getNode(fromTask);
        final Node toNode = getNode(toTask);

        fromNode.createRelationshipTo(toNode, DEPENDS_ON);
    }

    /**
     * Deletes the outgoing relationship fromTask -> toTask
     * @param fromTask
     * @param toTask
     * @return Returns true if dependency is found and deleted.
     */
    public boolean deleteTaskDependency(String fromTask, String toTask){
        final Node fromNode = getNode(fromTask);
        final Node toNode = getNode(toTask);

        boolean dependencyBroken = false;
        final Iterable<Relationship> relationships = fromNode.getRelationships(DEPENDS_ON, OUTGOING);
        for (Relationship relationship : relationships) {
            final Node endNode = relationship.getEndNode();
            if(getTaskName(endNode).equals(getTaskName(toNode))){
                relationship.delete();
                dependencyBroken = true;
            }
        }

        return dependencyBroken;
    }

    /**
     * Checks if a node exists in db with the given task name, and returns it.
     * If not found, an exception is thrown.
     * @param taskName
     * @return
     */
    public Node getNode(String taskName){
        final Node node = getNodeIfExists(taskName);
        if(node == null){
            throw new RuntimeException("Task NOT FOUND in the database: " + taskName);
        }
        return node;
    }

    /**
     * Checks if a node exists in db with the given task name, and returns it.
     * If not found, returns null.
     * @param taskName
     * @return
     */
    public Node getNodeIfExists(String taskName){
        return getNodeNameIndex().get(TASK_NAME, taskName).getSingle();
    }

    /**
     * Get task name from the given node
     * @param node
     * @return
     */
    public String getTaskName(Node node){
        return (String)node.getProperty(TASK_NAME);
    }

    /**
     * Get all the outgoing dependencies for the given node
     * @param node
     * @return
     */
    public Set<String> getDependentTaskNames(Node node){
        Set<String> dependsOn = Sets.newHashSet();
        for (Relationship relationship : node.getRelationships(DEPENDS_ON, OUTGOING)) {
            final Node dependentNode = relationship.getOtherNode(node);
            dependsOn.add(getTaskName(dependentNode));
        }
        return dependsOn;
    }

    /**
     * Extract all the properties from the given node, except the task name itself.
     * @param node
     * @return
     */
    public Map<String, Object> getTaskProperties(Node node){
        Map<String, Object> properties = new HashMap<>();
        for (String key : node.getPropertyKeys()) {
            if(!key.equals(TASK_NAME)){ //Don't need to add the task name property
                properties.put(key, node.getProperty(key));
            }
        }
        return properties;
    }

    /**
     * Query the db for the given task name and return as a Task object.
     * @param taskName
     * @return Task, or null if no task by the given name is found.
     */
    public Task getTask(String taskName){
        Node node = getNodeIfExists(taskName);
        if(node==null){
            return null;
        }

        return task(getTaskName(node))
                .dependsOn(getDependentTaskNames(node))
                .properties(getTaskProperties(node))
                .build();
    }

    /**
     * Begin a transaction
     * @return JTA-compliant Transaction object
     */
    public Transaction beginTransaction() {
        return Neo4jUtil.getInstance().beginTransaction();
    }

    @VisibleForTesting
    GraphDatabaseService getGraphDb(){
        return Neo4jUtil.getInstance().getGraphDb();
    }

    @VisibleForTesting
    Index<Node> getNodeNameIndex(){
        return Neo4jUtil.getInstance().getNodeNameIndex();
    }

}
