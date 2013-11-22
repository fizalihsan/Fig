package com.fig.config;

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

    private FigConfiguration(){ }

    public static FigConfiguration loadConfig(String configFile){
        Yaml yaml = new Yaml();
        FigConfiguration config = null;
        try( InputStream in = Files.newInputStream(Paths.get(configFile)) ) {//TODO load from classpath
            config = yaml.loadAs( in, FigConfiguration.class );
            System.out.println( config.toString() );
        } catch (Exception e){
            e.printStackTrace();
        }
        return config;
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
