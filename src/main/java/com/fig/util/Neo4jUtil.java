package com.fig.util;


import com.fig.config.FigConfiguration;
import com.fig.config.Neo4jConfig;
import com.google.common.annotations.VisibleForTesting;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main utility class to interact with the Neo4j Database.
 *
 * User: Fizal
 * Date: 11/21/13
 * Time: 10:21 PM
 */

public final class Neo4jUtil {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jUtil.class);
    @VisibleForTesting
    static final String TASK_NAME = "TASK_NAME";

    private GraphDatabaseService graphDb;
    private WrappingNeoServerBootstrapper webserver;
    private Index<Node> nodeNameIndex;

    private static volatile Neo4jUtil instance;

    @VisibleForTesting
    Neo4jUtil() {}

    static Neo4jUtil getInstance() {
        if(instance == null){
            synchronized (Neo4jUtil.class){
                if(instance == null){
                    Neo4jUtil tempInstance = new Neo4jUtil();
                    tempInstance.createGraphDb();
                    tempInstance.createNodeNameIndex();
                    tempInstance.registerShutdownHook();
                    tempInstance.enableWebserver();
                    instance = tempInstance;
                }
            }
        }
        return instance;
    }

    void createGraphDb(){
        final Neo4jConfig neo4jConfig = FigConfiguration.getInstance().getNeo4jConfig();
        String dbPath = neo4jConfig.getDbLocation();

        LOG.info("Creating graph database...");
        this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(dbPath)
//                    .setConfig(ShellSettings.remote_shell_enabled, "true")
//                    .setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
//                    .setConfig( GraphDatabaseSettings.node_keys_indexable, TASK_NAME)
                .newGraphDatabase();

    }

    @VisibleForTesting
    void enableWebserver(){
        final Neo4jConfig neo4jConfig = FigConfiguration.getInstance().getNeo4jConfig();
        final boolean enableWebserver = neo4jConfig.isEnableWebserver();
        if(enableWebserver){
            ServerConfigurator webserverConfig;
            webserverConfig = new ServerConfigurator((GraphDatabaseAPI) getGraphDb());
            // let the server endpoint be on a custom port
            webserverConfig.configuration().setProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575);

            this.webserver = new WrappingNeoServerBootstrapper((GraphDatabaseAPI) getGraphDb(), webserverConfig);

            //Neo4j web console or https://gephi.org/
            webserver.start();
        }
    }

    /**
     * Register a hook to shutdown the graph database when the JVM shuts down
     *
     * @param graphDb graph database to shutdown
     */
    private void registerShutdownHook() {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(getWebserver()!=null){
                    LOG.info("Shutting down web server...");
                    getWebserver().stop();
                }
                LOG.info("Shutting down graph database...");
                getGraphDb().shutdown();
            }
        });
    }

    /**
     * Begin a transaction
     * @return JTA-compliant Transaction object
     */
    Transaction beginTransaction() {
        return getGraphDb().beginTx();
    }

    Index<Node> getNodeNameIndex() {
        return nodeNameIndex;
    }

    @VisibleForTesting
    GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    @VisibleForTesting
    void setGraphDb(GraphDatabaseService graphDb){
        this.graphDb = graphDb;
    }

    @VisibleForTesting
    void createNodeNameIndex() {
        this.nodeNameIndex = getGraphDb().index().forNodes( TASK_NAME );
    }

    @VisibleForTesting
    public WrappingNeoServerBootstrapper getWebserver() {
        return webserver;
    }

    @VisibleForTesting
    static void setInstance(Neo4jUtil instance) {
        Neo4jUtil.instance = instance;
    }
}
