package com.fig.webservices;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/20/13
 * Time: 1:08 AM
 */
@ApplicationPath("/fig")
public class TaskApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();
        set.add(TaskResource.class);
        return set;
    }
}
