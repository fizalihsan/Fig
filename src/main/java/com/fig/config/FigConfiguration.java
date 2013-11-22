package com.fig.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/21/13
 * Time: 7:17 PM
 */
public class FigConfiguration {
    private Neo4jConfig neo4jConfig;
    private static FigConfiguration figConfiguration;
    //TODO remove this hardcoding
    private static final String CONFIG_FILE = "C:\\Fizal\\WorkArea\\SourceCode\\GitHubHome\\Fig\\src\\main\\resources\\figconfig.yaml";
    private static final Logger LOG = LoggerFactory.getLogger(FigConfiguration.class);

    private FigConfiguration(){ }

    public static FigConfiguration getInstance(){
        if(figConfiguration==null){
            figConfiguration = loadConfig(CONFIG_FILE);
            LOG.info("Configuration Properties: {}", figConfiguration );
        }
        return figConfiguration;
    }

    static FigConfiguration loadConfig(String configFile){
        try( InputStream in = Files.newInputStream(Paths.get(configFile)) ) {//TODO load from classpath
            return new Yaml().loadAs( in, FigConfiguration.class );
        } catch (Exception e){
            throw new RuntimeException("Error loading configuration file: " + configFile, e);
        }
    }

    public Neo4jConfig getNeo4jConfig() {
        return neo4jConfig;
    }

    public void setNeo4jConfig(Neo4jConfig neo4jConfig) {
        this.neo4jConfig = neo4jConfig;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FigConfiguration{");
        sb.append(neo4jConfig);
        sb.append('}');
        return sb.toString();
    }
}
