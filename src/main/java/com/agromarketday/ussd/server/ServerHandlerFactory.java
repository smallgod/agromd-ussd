/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.server;

import com.agromarketday.ussd.logger.LoggerUtil;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

/**
 *
 * @author smallgod
 */
public class ServerHandlerFactory {

    private final LoggerUtil logger;

    protected ServerHandlerFactory() {
        this.logger = new LoggerUtil(ServerHandlerFactory.class);
    }

    protected Handler createWebAppContextHandler(String contextPath, String resourceBase, String webDescriptor) {

        WebAppContext webAppHandler = new WebAppContext();

        // Add the filter, and then use the provided FilterHolder to configure it
        FilterHolder cors = webAppHandler.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        webAppHandler.setContextPath(contextPath);

        // webAppHandler.setWar(ApplicationPropertyLoader.getInstance().getJarFolderName() + "/src/main/recontool/war/recontool.war");
        // webAppHandler.setResourceBase("/home/smallgod/NetBeansProjects/recontool/src/main/recontool/"); //get app dir variable location
        // webAppHandler.setDescriptor("/home/smallgod/NetBeansProjects/src/main/recontool/WEB-INF/web.xml");
        // webAppHandler.setResourceBase(ApplicationPropertyLoader.getInstance().getJarFolderName() + "/src/main/recontool/"); //get app dir variable location
        webAppHandler.setResourceBase(resourceBase); //get app dir variable location
        webAppHandler.setDescriptor(webDescriptor);

        webAppHandler.setConfigurations(new Configuration[]{
            
            new WebInfConfiguration(),
            new WebXmlConfiguration(),
            new MetaInfConfiguration(),
            new FragmentConfiguration(),
            new EnvConfiguration(),
            new PlusConfiguration(),
            new AnnotationConfiguration(),
            new JettyWebXmlConfiguration()});

        // webAppHandler.setDefaultDescriptor(ApplicationPropertyLoader.getInstance().getJarFolderName() + "/src/main/recontool/webdefault/webdefault.xml"); //copy from The location is JETTY_HOME/etc/webdefault.xml     
        webAppHandler.setParentLoaderPriority(true);//make the ClassLoader behavior more akin to standard Java (as opposed to the reverse requirements for Servlets)

        // Configuration configs[] = new Configuration[]{new FragmentConfiguration(), new PlusConfiguration(), new EnvConfiguration() };
        // context.setConfigurations(configs);
        logger.debug("Resource base: " + webAppHandler.getBaseResource());
        logger.debug("Context path : " + webAppHandler.getContextPath());

        return webAppHandler;
    }

    /**
     * Looks for a matching file in the local directory to serve
     *
     * @param welcomeFiles
     * @param resourceBase
     * @param isDirectoriesListed
     * @return
     */
    protected Handler createResourceHandler(String[] welcomeFiles, String resourceBase, Boolean isDirectoriesListed) {

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(isDirectoriesListed);
        resourceHandler.setWelcomeFiles(welcomeFiles);
        resourceHandler.setResourceBase(resourceBase);

        return resourceHandler;

    }

    /**
     * A ContextHandler is a HandlerWrapper that responds only to requests that
     * have a URI prefix that matches the configured context path. Requests that
     * match the context path have their path methods updated accordingly, and
     * the following optional context features applied as appropriate: A Thread
     * Context classloader. A set of attributes A set of init parameters A
     * resource base (aka document root) A set of virtual host names. Requests
     * that don't match are not handled.
     *
     * @param welcomeFiles
     * @param resourceBase
     * @param contextPath
     * @return
     */
    protected Handler createContextHandler(String[] welcomeFiles, String resourceBase, String contextPath) {

        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setWelcomeFiles(welcomeFiles);
        contextHandler.setContextPath(contextPath);
        contextHandler.setResourceBase(resourceBase);
        contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());

        return contextHandler;
    }

    /**
     * A ServletContextHandler is a specialization of ContextHandler with
     * support for standard servlets.
     *
     * @param welcomeFiles
     * @param resourceBase
     * @param contextPath
     * @return
     */
    protected Handler createServletContextHandler(String[] welcomeFiles, String resourceBase, String contextPath) {

        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath(contextPath);
        servletHandler.setResourceBase(resourceBase);
        servletHandler.setWelcomeFiles(welcomeFiles);

        //servletHandler.addServlet(new ServletHolder(new HelloServlet()), "/*");
        //servletHandler.addServlet(new ServletHolder(new HelloServlet("Buongiorno Mondo")), "/it/*");
        //servletHandler.addServlet(new ServletHolder(new HelloServlet("Bonjour le Monde")), "/fr/*");
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        return servletHandler;
    }

    /**
     *
     * @param welcomeFiles
     * @param resourceBase
     * @param contextPath
     * @param multipartServlet
     * @param tempFolder
     * @return
     */
    protected Handler createMultipartServletContextHandler(String[] welcomeFiles, String resourceBase, String contextPath, Servlet multipartServlet, String tempFolder) {

        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath(contextPath);
        servletHandler.setResourceBase(resourceBase);
        servletHandler.setWelcomeFiles(welcomeFiles);

        //servletHandler.addServlet(new ServletHolder(new HelloServlet()), "/*");
        //servletHandler.addServlet(new ServletHolder(new HelloServlet("Buongiorno Mondo")), "/it/*");
        //servletHandler.addServlet(new ServletHolder(new HelloServlet("Bonjour le Monde")), "/fr/*");
        ServletHolder fileUploadServletHolder = new ServletHolder(multipartServlet);
        fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(tempFolder));

        servletHandler.addServlet(fileUploadServletHolder, "/uploadfile");

        return servletHandler;
    }

    /**
     * A Web Applications contextHandler is a variation of ServletContextHandler
     * that uses the standard layout and web.xml to configure the servlets,
     * filters and other features
     *
     * @param welcomeFiles
     * @param resourceBase
     * @param contextPath
     * @param warFile
     * @return
     */
    protected Handler createWebAppContextHandlerProd(String[] welcomeFiles, String resourceBase, String contextPath, String warFile) {

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(contextPath);
        webapp.setResourceBase(resourceBase);
        webapp.setWelcomeFiles(welcomeFiles);
        webapp.setWar(warFile);

        return webapp;
    }

}
