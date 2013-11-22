package com.fig.config;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/21/13
 * Time: 7:21 PM
 */
public class Neo4jConfig {
    private String dbLocation;
    private boolean enableWebserver;

    public String getDbLocation() {
        return dbLocation;
    }

    public void setDbLocation(String dbLocation) {
        this.dbLocation = dbLocation;
    }

    public boolean isEnableWebserver() {
        return enableWebserver;
    }

    public void setEnableWebserver(boolean enableWebserver) {
        this.enableWebserver = enableWebserver;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Neo4jConfig{");
        sb.append("dbLocation='").append(dbLocation).append("\',");
        sb.append("enableWebserver='").append(enableWebserver).append("\'");
        sb.append('}');
        return sb.toString();
    }
}
