package com.fig.domain;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Represents a task and its dependencies
 * User: Fizal
 * Date: 11/20/13
 * Time: 4:18 PM
 */
public class Task {
    private String name;
    private Set<String> dependsOn;
    private Map<String, Object> properties;

    //use builder to construct
    Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = Maps.newHashMap(properties);
    }

    public Set<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(Collection<String> dependsOn) {
        this.dependsOn = Sets.newHashSet(dependsOn);
    }

    /**
     * Method to remove task properties with null as value. Neo4j nodes do not accept null values in properties.
     * @param task
     */
    public void dropNullValueProperties(){
        if(properties!=null){
            final Iterator<Map.Entry<String,Object>> iterator = properties.entrySet().iterator();
            for (;iterator.hasNext();) {
                final Object value = iterator.next().getValue();
                if(value == null){
                    iterator.remove();
                }
            }
        }
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
        sb.append(", dependsOn=").append(dependsOn);
        sb.append(", properties=").append(properties);
        sb.append('}');
        return sb.toString();
    }
}
