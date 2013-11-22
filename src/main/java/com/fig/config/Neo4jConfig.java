package com.fig.config;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/21/13
 * Time: 7:21 PM
 */
public class Neo4jConfig {
    private String dbLocation;

    public String getDbLocation() {
        return dbLocation;
    }

    public void setDbLocation(String dbLocation) {
        this.dbLocation = dbLocation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Neo4jConfig{");
        sb.append("dbLocation='").append(dbLocation).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
