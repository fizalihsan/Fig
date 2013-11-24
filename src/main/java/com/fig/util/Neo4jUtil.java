package com.fig.util;


import com.fig.config.FigConfiguration;
import com.fig.config.Neo4jConfig;
import com.fig.domain.Task;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.fig.domain.TaskBuilder.task;
import static com.fig.domain.TaskRelations.DEPENDS_ON;

/**
 * Main utility class to interact with the Neo4j Database.
 *
 * User: Fizal
 * Date: 11/21/13
 * Time: 10:21 PM
 */

public final class Neo4jUtil {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jUtil.class);
    private static final String TASK_NAME = "TASK_NAME";
    private GraphDatabaseService graphDb;
    private WrappingNeoServerBootstrapper webserver;
    private Index<Node> nodeNameIndex;

    private static volatile Neo4jUtil instance;

    //Privatized constructor to restrict access from clients
    private Neo4jUtil() {}

    public static Neo4jUtil getInstance() {
        if(instance == null){
            synchronized (Neo4jUtil.class){
                if(instance == null){
                    Neo4jUtil tempInstance = new Neo4jUtil();
                    tempInstance.createGraphDb();
                    instance = tempInstance;
                }
            }
        }
        return instance;
    }

    /**
     * Create a graph database
     *
     */
    void createGraphDb() {
        final Neo4jConfig neo4jConfig = FigConfiguration.getInstance().getNeo4jConfig();
        String dbPath = neo4jConfig.getDbLocation();
        try {
            LOG.info("Creating graph database...");
            this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(dbPath)
//                    .setConfig(ShellSettings.remote_shell_enabled, "true")
//                    .setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
//                    .setConfig( GraphDatabaseSettings.node_keys_indexable, TASK_NAME)
                    .newGraphDatabase();

            this.nodeNameIndex = graphDb.index().forNodes( TASK_NAME );
            registerShutdownHook(this.graphDb, this.webserver);

            final boolean enableWebserver = neo4jConfig.isEnableWebserver();
            if(enableWebserver){
                ServerConfigurator webserverConfig;
                webserverConfig = new ServerConfigurator((GraphDatabaseAPI) graphDb);
                // let the server endpoint be on a custom port
                webserverConfig.configuration().setProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575);

                webserver = new WrappingNeoServerBootstrapper((GraphDatabaseAPI) graphDb, webserverConfig);

                //Neo4j web console or https://gephi.org/
                webserver.start();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating graph database", e);
        }
    }

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
        final Iterable<Relationship> relationships = fromNode.getRelationships(DEPENDS_ON, Direction.OUTGOING);
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
        for (Relationship relationship : node.getRelationships(DEPENDS_ON, Direction.OUTGOING)) {
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
     * Register a hook to shutdown the graph database when the JVM shuts down
     *
     * @param graphDb graph database to shutdown
     */
    private void registerShutdownHook(final GraphDatabaseService graphDb, final WrappingNeoServerBootstrapper webserver) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(webserver!=null){
                    LOG.info("Shutting down web server...");
                    webserver.stop();
                }
                LOG.info("Shutting down graph database...");
                graphDb.shutdown();
            }
        });
    }

    /**
     * Begin a transaction
     * @return JTA-compliant Transaction object
     */
    public Transaction beginTransaction() {
        return getGraphDb().beginTx();
    }

    @VisibleForTesting
    GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    @VisibleForTesting
    Index<Node> getNodeNameIndex() {
        return nodeNameIndex;
    }
}
