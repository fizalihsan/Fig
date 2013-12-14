package com.fig.util;


/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/19/13
 * Time: 7:20 PM
 */
public class WebserverUtil {
    /*private static final Logger LOG = LoggerFactory.getLogger(WebserverUtil.class);

    private final int httpPort;
    private final Server server;
    private final ContextHandlerCollection contexts = new ContextHandlerCollection();

    public WebserverUtil(int httpPort){
        this.httpPort = httpPort;
        server = new Server(this.httpPort);
//        server.setHandler(contexts);
        registerShutdownHook();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder servletHolder = new ServletHolder(new ServletContainer());
//        servletHolder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.fig.webservices");
        servletHolder.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, "com.fig.webservices.MainApplication");
        servletHolder.setInitOrder(0);

        context.addServlet(servletHolder, "*//*");
    }


    public void addHandler(Handler handler, String contextPath){
        LOG.info("Adding handler to server: {}", handler.getClass().getCanonicalName() );
        ContextHandler context = new ContextHandler();
        context.setContextPath(contextPath);
        context.setHandler(handler);
        contexts.addHandler(context);
    }

    public void addHandler(Handler handler){
        LOG.info("Adding handler to server: {}", handler.getClass().getCanonicalName() );
        contexts.addHandler(handler);
    }

    *//**
     * Start the webserver.
*//*
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

    *//**
     * Register a hook to shutdown the web server when the JVM shuts down
     *
    *//*
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
                        server.shutdown();
                    } catch (Exception e) {
                        LOG.error("Error occurred while stopping JETTY web server: ", e);
                    }
                }
            }
        });
    }

    *//**
     * Returns a test handler which prints "Hello World"
     * @return
    *//*
    public static Handler getHelloWorldHandler(){
        return new AbstractHandler() {
            private void printRequest(HttpServletRequest request){
                final Map parameterMap = request.getParameterMap();
                for (Object key : parameterMap.keySet()) {
                    LOG.info("RequestParameter : {} = {}", key, parameterMap.get(key));
                }
            }

            *//*
             * Handler that returns a static "Hello World" text
             * @param target the target of the request, which is either a URI or a name from a named dispatcher.
             * @param baseRequest the Jetty mutable request object, which is always unwrapped.
             * @param request the immutable request object, which may have been wrapped by a filter or servlet.
             * @param response the response, which may have been wrapped by a filter or servlet.
             * @throws IOException
             * @throws ServletException
            *//*
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                printRequest(request);
                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("<h1>Hello World</h1>");
                baseRequest.setHandled(true);
            }
        };
    }*/
}
