package com.fig.util;

/*import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;*/

/**
 * Comment here about the class
 * User: Fizal
 * Date: 11/30/13
 * Time: 8:31 PM
 */
public class TomcatEmbeddedServer {
    /*private static final Logger LOG = LoggerFactory.getLogger(TomcatEmbeddedServer.class);
    public static void main(String[] args) {
        new TomcatEmbeddedServer().tomcat();
    }

    private void tomcat(){
        Tomcat tomcat = new MyTomcat();
        tomcat.setPort(1234);

//        final InputStream inputStream = this.getClass().getResourceAsStream("/web.xml");
//        ContextConfig contextConfig = new ContextConfig();
//        contextConfig.setDefaultWebXml("/web.xml");

        *//*try {
            final Context context = tomcat.addWebapp("/fig2", new File(".").getAbsolutePath());
            context.getServletContext().setAttribute(Globals.ALT_DD_ATTR, "C:\\Fizal\\WorkArea\\SourceCode\\GitHubHome\\Fig\\src\\main\\webapp\\WEB-INF\\web.xml");
        } catch (ServletException e) {
            e.printStackTrace();
        }*//*
        Context ctx = tomcat.addContext("/Fig-0.0.2", new File(".").getAbsolutePath());

        tomcat.addServlet(ctx, "hello", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                Writer writer = resp.getWriter();
                writer.write("Hello World");
                writer.flush();
            }
        });

        ctx.addServletMapping("*//*", "hello");

        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    private static class MyTomcat extends Tomcat{
        @Override
        public Context addWebapp(String contextPath, String baseDir) throws ServletException {
            Context context = null;
            String pathToGlobalWebXml = "C:\\Fizal\\WorkArea\\SourceCode\\GitHubHome\\Fig\\src\\main\\webapp\\WEB-INF\\web.xml";
            String webappFilePath = "C:\\Temp\\webapp";
            try {
                context = new StandardContext();
                context.setName(contextPath);
                context.setPath(contextPath);
                context.setDocBase(webappFilePath);
                context.setRealm(this.getHost().getRealm());
                ContextConfig contextConfig = new ContextConfig();
                context.addLifecycleListener(contextConfig);
                if (new File(pathToGlobalWebXml).exists()) {
                    contextConfig.setDefaultWebXml(pathToGlobalWebXml);
                } else {
                    contextConfig.setDefaultWebXml("org/apache/catalin/startup/NO_DEFAULT_XML");
                }
                host.addChild(context);
            } catch (Exception e) {
                LOG.error("Error while deploying webapp", e);
            }

            return context;
        }
    }*/
}
