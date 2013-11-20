package com.fig.util;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/19/13
 * Time: 7:20 PM
 */
public class WebserverUtil {
    private static final Logger LOG = LoggerFactory.getLogger(WebserverUtil.class);

    private final int httpPort;
    private final Server server;
    private final ContextHandlerCollection contexts = new ContextHandlerCollection();

    public WebserverUtil(int httpPort){
        this.httpPort = httpPort;
        server = new Server(this.httpPort);
        server.setHandler(contexts);
        registerShutdownHook();
    }

    public void addHandler(Handler handler, String contextPath){
        LOG.info("Adding handler to server: {}", handler.getClass().getCanonicalName() );
        ContextHandler context = new ContextHandler();
        context.setContextPath(contextPath);
        context.setHandler(handler);
        contexts.addHandler(context);
    }

    public void start(){
        //TODO don't start the server if no handlers are registered
        try {
            server.start();
            server.join();//TODO why do we need this server.join?
            LOG.info("JETTY webserver started on HTTP port: {}", this.httpPort);
        } catch (Exception e) {
            throw new RuntimeException("Error starting JETTY webserver on port: " + this.httpPort, e);
        }
    }

    /**
     * Register a hook to shutdown the web server when the JVM shuts down
     *
     */
    private void registerShutdownHook() {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(server!=null){
                    LOG.info("Shutting down JETTY web server...");
                    try {
                        server.stop();
                    } catch (Exception e) {
                        LOG.error("Error occurred while stopping JETTY web server: ", e);
                    }
                }
            }
        });
    }

    //TODO reduce scope of the method
    public static Handler getHandler(){
        return new AbstractHandler() {
            private void printRequest(HttpServletRequest request){
                final Map parameterMap = request.getParameterMap();
                for (Object key : parameterMap.keySet()) {
                    LOG.info("{} = {}", key, parameterMap.get(key));
                }
            }

            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                printRequest(request);
                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("<h1>Hello</h1>");
                ((Request)request).setHandled(true);
            }
        };
    }
}
