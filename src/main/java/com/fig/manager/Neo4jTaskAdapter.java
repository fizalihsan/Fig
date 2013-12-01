package com.fig.manager;

import com.fig.domain.Task;
import com.fig.domain.TaskRelations;
import com.fig.exception.CyclicDependencyException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.fig.domain.FigConstants.TASK_NAME;
import static com.fig.domain.TaskBuilder.task;
import static com.fig.domain.TaskRelations.DEPENDS_ON;
import static java.lang.Integer.MAX_VALUE;
import static org.neo4j.graphdb.Direction.OUTGOING;
import static org.neo4j.kernel.Traversal.expanderForTypes;
import static org.neo4j.kernel.Traversal.pathToString;

/**
 * Adapter class between the Neo4jHelper and the rest of the application. It restricts the leakage of Neo4j related
 * classes to rest of the module.
 * User: Fizal
 * Date: 11/24/13
 * Time: 6:54 PM
 */
public class Neo4jTaskAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jTaskAdapter.class);

    private static final PathPrinter TASK_PATH_PRINTER = new PathPrinter(TASK_NAME);
    private static final PathFinder<Path> OUTGOING_DEPENDENCY_FINDER = GraphAlgoFactory.allSimplePaths(expanderForTypes(DEPENDS_ON, OUTGOING), MAX_VALUE);

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
     * Update the properties in the given task. If the task has any property with null as value, then it will be
     * removed from the Neo4j node properties.
     * @param task
     */
    public void updateTaskProperties(Task task){
        final Node node = getNode(task.getName());

        for (Map.Entry<String, Object> entry : task.getProperties().entrySet()) {
            if(entry.getValue()==null){
                node.removeProperty(entry.getKey());
            } else {
                node.setProperty(entry.getKey(), entry.getValue());
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
        //Restrict self-dependency
        checkForSelfLoop(fromTask, toTask);

        //Check for any path from toTask to fromTask to avoid loops.
        checkForLoops(fromTask, toTask);

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

    Task getTask(Node node){
        return task(getTaskName(node))
                .dependsOn(getDependentTaskNames(node))
                .properties(getTaskProperties(node))
                .build();
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
        return node==null?null:getTask(node);
    }

    /**
     * Executes the cypher
     * @param cypher
     * @return
     */
    public String executeCypher(String cypher){
        return Neo4jHelper.getInstance().executeCypher(cypher);
    }

    //TODO document this method
    public void getAncestors(String task){
        final Traverser traverser = getTraverser(task, Direction.OUTGOING);
        for (Path path : traverser) {
            System.out.println(pathToString(path, TASK_PATH_PRINTER));
        }
        System.out.println("----------------------------------------------");
    }

    //TODO document this method
    public void getDescendants(String task){
        final Traverser traverser = getTraverser(task, Direction.INCOMING);
        for (Path path : traverser) {
            System.out.println(pathToString(path, TASK_PATH_PRINTER));
        }
        System.out.println("----------------------------------------------");
    }

    //TODO document this method
    public Traverser getTraverser(String task, Direction direction){
        TraversalDescription td = Traversal.description()
                .breadthFirst()
                .relationships( TaskRelations.DEPENDS_ON, direction )
                .evaluator(Evaluators.excludeStartPosition());
        return td.traverse(getNode(task));
    }

    //TODO document this method
    public Iterator<Path> getPathsBetweenTasks(String fromTask, String toTask){
        final Iterable<Path> paths = OUTGOING_DEPENDENCY_FINDER.findAllPaths(getNode(fromTask), getNode(toTask));
        return paths.iterator();
    }

    /**
     * Begin a transaction
     * @return JTA-compliant Transaction object
     */
    public Transaction beginTransaction() {
        return Neo4jHelper.getInstance().beginTransaction();
    }

    @VisibleForTesting
    GraphDatabaseService getGraphDb(){
        return Neo4jHelper.getInstance().getGraphDb();
    }

    @VisibleForTesting
    Index<Node> getNodeNameIndex(){
        return Neo4jHelper.getInstance().getNodeNameIndex();
    }

    //TODO document this method
    @VisibleForTesting
    void checkForSelfLoop(String fromTask, String toTask){
        if(fromTask.equals(toTask)){
            //cyclic dependency detected
            throw new CyclicDependencyException(
                    Joiner.on("").join("Unable to create dependency ", fromTask, " -> ", toTask,
                            " since there are one and the same.")
            );
        }
    }

    //TODO document this method
    @VisibleForTesting
    void checkForLoops(String fromTask, String toTask){
        final Iterator<Path> pathsBetweenTasks = getPathsBetweenTasks(toTask, fromTask);
        if(pathsBetweenTasks.hasNext()){
            //cyclic dependency detected
            StringBuilder errorMsg = new StringBuilder().append(
                    Joiner.on("").join("Unable to create dependency ", fromTask, " -> ", toTask,
                            " since there are existing paths from ", toTask, " -> ", fromTask, ": ")
            );

            for (; pathsBetweenTasks.hasNext(); ) {
                final Path path = pathsBetweenTasks.next();
                errorMsg.append(pathToString(path, TASK_PATH_PRINTER));
            }
            throw new CyclicDependencyException(errorMsg.toString());
        }
    }
}
