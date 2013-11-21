package com.fig.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/20/13
 * Time: 4:18 PM
 */
public class Task {
    private String name;
    private Set<String> dependsOn;
    private Map<String, String> properties = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Set<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(Set<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (name != null ? !name.equals(task.name) : task.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("name='").append(name).append('\'');
        sb.append(", properties=").append(properties);
        sb.append(", dependsOn=").append(dependsOn);
        sb.append('}');
        return sb.toString();
    }
}
