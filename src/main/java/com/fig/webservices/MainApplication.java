package com.fig.webservices;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Main JAX RS class that is loaded by the web container during start up. This class registers all the web resources
 * to serve.
 * User: Fizal
 * Date: 11/20/13
 * Time: 1:08 AM
 */
@ApplicationPath("/fig")
public class MainApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();
        set.add(TaskResource.class);
        set.add(StatusResource.class);
        return set;
    }
}
