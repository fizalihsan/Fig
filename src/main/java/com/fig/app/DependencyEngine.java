package com.fig.app;

import com.fig.util.WebserverUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents the heart of the system which is the Dependency Engine.
 * User: Fizal
 * Date: 11/19/13
 * Time: 6:27 PM
 */
public class DependencyEngine {
    private static final Logger logger = LoggerFactory.getLogger(DependencyEngine.class);

    public static void main(String[] args) {
        logger.debug("Hello World...");
        final DependencyEngine engine = new DependencyEngine();
        engine.startWebserver();
    }

    /**
     *
     */
    void startWebserver(){
        final WebserverUtil webserverUtil = new WebserverUtil(8080);
        webserverUtil.addHandler(WebserverUtil.getHandler(), "/test");
        webserverUtil.start();
    }
}
