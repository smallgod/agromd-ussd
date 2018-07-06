package com.agromarketday.ussd;

import com.agromarketday.ussd.config.UssdMenuConfig;
import com.agromarketday.ussd.config.ConfigLoader;
import com.agromarketday.ussd.config.DatabaseConfig;
import com.agromarketday.ussd.config.HibernateConfig;
import com.agromarketday.ussd.config.HttpClientPoolConfig;
import com.agromarketday.ussd.config.JettyServerConfig;
import com.agromarketday.ussd.config.JobsConfig;
import com.agromarketday.ussd.config.RemoteUnitConfig;
import com.agromarketday.ussd.connect.HttpClientPool;
import com.agromarketday.ussd.constant.APIContentType;
import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.controller.JsonAPIServer;
import com.agromarketday.ussd.controller.JsonProcessor;
import com.agromarketday.ussd.controller.XmlProcessor;
import com.agromarketday.ussd.database.CustomHibernate;
import com.agromarketday.ussd.datamodel.datawrappers.AppConfigWrapper;
import com.agromarketday.ussd.datamodel.jaxb.config.Appconfig;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.logger.LoggerUtil;
import com.agromarketday.ussd.scheduler.CustomJobScheduler;
import com.agromarketday.ussd.server.CustomJettyServer;
import com.agromarketday.ussd.sharedInterface.SharedAppConfigIF;
import com.agromarketday.ussd.util.BindXmlAndPojo;
import static com.agromarketday.ussd.util.GeneralUtils.shutdownProcessor;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationException;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author smallgod
 */
public final class AppEntry implements Daemon, ServletContextListener {

    private static DaemonContext daemonContext;
    private static LoggerUtil LOGGER;
    private static SharedAppConfigIF appConfigWrapper;
    private static HttpClientPool clientPool;

    private static ExecutorService taskExecutorService;
    private static final int NUM_OF_THREADS = 20;
    private static CustomHibernate internalDbAdapter;
    private CustomJobScheduler jobScheduler;
    private CustomJettyServer jettyServer;
    private static XmlProcessor xmlProcessor;
    private static JsonProcessor jsonProcessor;
     private static JsonAPIServer jsonApiServer;

    //@deprecated As of 2.4, use {@link MainMapLookup#setMainArguments(String[])} 
    @Override
    public void init(DaemonContext context) {

        try {

            daemonContext = context;

            //configs
            setAppConfigWrapper(loadConfigs());

            LOGGER = new LoggerUtil(AppEntry.class);

            LOGGER.debug("TEST DEBUG!!! ");
            LOGGER.info(" TEST INFO!!!! ");
            LOGGER.warn(" TEST WARN!!!! ");
            LOGGER.error("TEST ERROR!!! ");

            ConfigLoader configLoader = new ConfigLoader(getAppConfigWrapper());

            HibernateConfig hibernateConfig = configLoader.getHibernateConfig();

            DatabaseConfig databaseConfig = configLoader.getDatabaseConfig();
            HttpClientPoolConfig httpClientConfig = configLoader.getHttpClientPoolConfig();
            JettyServerConfig serverConfig = configLoader.getJettyServerConfig();

            JobsConfig adPaymentProcessorConfig = configLoader.getAdPaymentJobConfig();
            JobsConfig adCampaignProcessorConfig = configLoader.getAdCampaignProcessorJobConfig();
            JobsConfig adDisplayProcessorConfig = configLoader.getAdDisplayJobConfig();
            JobsConfig midnightCallConfig = configLoader.getMidnightCallJobConfig();

            UssdMenuConfig ussdMenuLoader = configLoader.getUssdMenuDirConfig();
            RemoteUnitConfig remoteUnitConfig = configLoader.getRemoteUnitConfig();

            //taskExecutorService = Executors.newSingleThreadExecutor();
            taskExecutorService = Executors.newFixedThreadPool(NUM_OF_THREADS);

            clientPool = new HttpClientPool(httpClientConfig, APIContentType.JSON);

            internalDbAdapter = new CustomHibernate(hibernateConfig);

            //setup Jetty Server last, because we need to pass some params into the context initialised method
            jettyServer = new CustomJettyServer(serverConfig);
            jettyServer.initialiseServer();

            //jobScheduler = new CustomJobScheduler(clientPool, internalDbAdapter, externalDbAccessAdapter);
            xmlProcessor = new XmlProcessor(internalDbAdapter, clientPool, ussdMenuLoader, remoteUnitConfig, adCampaignProcessorConfig, adPaymentProcessorConfig, adDisplayProcessorConfig, taskExecutorService, jobScheduler);
            jsonProcessor = new JsonProcessor(internalDbAdapter, clientPool, ussdMenuLoader, remoteUnitConfig, adCampaignProcessorConfig, adPaymentProcessorConfig, adDisplayProcessorConfig, taskExecutorService, jobScheduler);
            jsonApiServer = new JsonAPIServer(internalDbAdapter);

            //jobScheduler.scheduleARepeatJob(adCampaignProcessorConfig, adPaymentProcessorConfig, adDisplayProcessorConfig, AdCampaignProcessorJob.class, new AdCampaignProcessorJobListener(), NamedConstants.CAMPAIGN_JOB_SCHEDULE_START_DELAY);
        } catch (DaemonInitException | FileNotFoundException | UnsupportedEncodingException | SAXException | JAXBException | NullPointerException ex) {
            ex.printStackTrace();
            failDaemon("Exception initialising daemon", ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            failDaemon("General error initialising daemon", ex);
        } catch (MyCustomException ex) {
            ex.printStackTrace();
            failDaemon("General error initialising daemon");
        }

    }

    @Override
    public void start() {

        try {

            if (!jettyServer.startServer()) {

                failDaemon("Failed to start local JettyServer, terminating... ");

            } else {

                boolean dbInit = internalDbAdapter.initialiseDBResources();

                if (!dbInit) {
                    failDaemon("Failed to initialised Database, hibernate sessionFactory is NULL");

                } else {

                    //Add initialising data
                    InitialiseDBData init = new InitialiseDBData(internalDbAdapter);
                    init.initDB();
                    //init.gummaaza();
                }
            }

            //Future executingProcess = processorService.submit(paymentProcessor);
            //server.join(); //think of putting this inside an executor so that it doesn't hang
        } catch (Exception ex) {

            ex.printStackTrace();

            failDaemon("Error starting jetty server and daemon...", ex);

        } catch (MyCustomException ex) {

            ex.printStackTrace();

            failDaemon("Failed to start local JettyServer, terminating: " + ex.getMessage());
        }
    }

    @Override
    public void stop() {

        try {
            LOGGER.debug("Stopping Job(s), Server, http connections & Daemon...");
            jettyServer.stopServer();
            //jobScheduler.cancelAllJobs();
            clientPool.releaseHttpResources();
            internalDbAdapter.releaseDBResources();
            shutdownProcessor(taskExecutorService, NamedConstants.SHUTDOWN_DELAY, TimeUnit.MINUTES); //shutdown the executor service

        } catch (Exception ex) {
            LOGGER.error("Error occurred while stopping daemon: " + ex.getMessage());
        }

    }

    @Override
    public void destroy() {
    }

    private SharedAppConfigIF loadConfigs() throws FileNotFoundException, UnsupportedEncodingException, SAXParseException, SAXException, ValidationException, JAXBException, NullPointerException, Exception, MyCustomException {

        String profile; // this is the first commandline argument defined in the .sh file of this daemon in this case either development, production or ....
        String projectDir;
        String configsDir;
        String configsXsdFile;
        String configsXmlFile;
        String logsDir;
        String ussdMenuDir;

        String[] cmdArgs = loadCmdLineArgs();

        if (cmdArgs.length > 6) {

            profile = cmdArgs[0]; // this is the first commandline argument defined in the .sh file of this daemon in this case either development, production or ....
            projectDir = cmdArgs[1];
            configsDir = cmdArgs[2];
            configsXsdFile = cmdArgs[3];
            configsXmlFile = cmdArgs[4];
            logsDir = cmdArgs[5];
            ussdMenuDir = cmdArgs[6];

        } else {
            failDaemon("Expected more than 5 commandline args in the startup script");
            return null;
        }

        //props
        Appconfig appConfig = (Appconfig) loadAppProps(configsXmlFile, configsXsdFile, Appconfig.class);
        SharedAppConfigIF newAppConfigWrapper = new AppConfigWrapper(appConfig, configsDir, logsDir, projectDir, ussdMenuDir, profile);

        //log4j
        String log4jFile = newAppConfigWrapper.getLog4JpropsAbsPath();
        loadLog4JProps(log4jFile, logsDir);

        return newAppConfigWrapper;

    }

    /**
     * @return the appConfigWrapper
     */
    public SharedAppConfigIF getAppConfigWrapper() {

        return appConfigWrapper;
    }

    /**
     * @param aAppConfigWrapper the appConfigWrapper to set
     */
    private void setAppConfigWrapper(SharedAppConfigIF aAppConfigWrapper) {
        appConfigWrapper = aAppConfigWrapper;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("AppEntry's contextInitialized method called: " + sce.getClass().getName());

        ServletContext context = sce.getServletContext();

        if (xmlProcessor != null) {
            context.setAttribute(NamedConstants.USSD_SERVER_XML_HANDLER, xmlProcessor);
        } else {
            System.err.println("ContextInitialised called, but XML PROCESSOR is NULL, terminating");
            System.exit(0);
        }

        if (jsonProcessor != null) {
            context.setAttribute(NamedConstants.USSD_SERVER_JSON_HANDLER, jsonProcessor);
        } else {
            System.err.println("ContextInitialised called, but JSON PROCESSOR is NULL, terminating");
            System.exit(0);
        }
        
        if (jsonApiServer != null) {
            context.setAttribute(NamedConstants.JSON_API_SERVER_HANDLER, jsonApiServer);
        } else {
            System.err.println("ContextInitialised called, but JSON API server is NULL, terminating");
            System.exit(0);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("AppEntry's contextDestroyed method called: " + sce.getClass().getName());
    }

    /**
     * Get the context of the daemon if running
     *
     * @return DaemonContext
     * @throws NullPointerException if daemon context is not available
     */
    public DaemonContext getDaemonContext() throws NullPointerException { //change to my custom exception
        return AppEntry.daemonContext;
    }

    protected <T> Object loadAppProps(String xmlFilePath, String xsdFilePath, Class<T> classToBind) throws FileNotFoundException, UnsupportedEncodingException, SAXException, ValidationException, JAXBException, NullPointerException, MyCustomException {

        Object appConfigsJaxb = BindXmlAndPojo.xmlFileToObject(xmlFilePath, xsdFilePath, classToBind);
        return appConfigsJaxb;

    }

    /**
     * Load log4j properties
     *
     * @param log4jFile
     * @param paramsToPass - logs dir path is passed at index 0, others can
     * follow
     * @throws java.lang.Exception
     */
    protected void loadLog4JProps(String log4jFile, String... paramsToPass) throws Exception {
        //protected void loadLog4JProps(String log4jFile, String... paramsToPass) throws Exception {

        //Properties props = new Properties();
        //props.put("logsFolder", paramsToPass);
        //DOMConfigurator.setParameter(elem, propSetter, props);
        ///////////
        //DOMConfigurator.configure(log4jFile); //XML configurator
        //MapLookup.setMainArguments(paramsToPass);
        ///////////
        //PropertyConfigurator.configure(log4jPropsFileLoc);//Property file configurator
    }

    /**
     *
     * @param log4jFile
     * @param logDir
     * @throws Exception
     */
    protected void loadLog4JProps(String log4jFile, String logDir) throws Exception {
        //protected void loadLog4JProps(String log4jFile, String... paramsToPass) throws Exception {

        //Properties props = new Properties();
        //props.put("logsFolder", paramsToPass);
        //DOMConfigurator.setParameter(elem, propSetter, props);
        ///////////
        //DOMConfigurator.configure(log4jFile); //XML configurator
        //MapLookup.setMainArguments(paramsToPass);
        ///////////
        //PropertyConfigurator.configure(log4jPropsFileLoc);//Property file configurator
        LoggerUtil.configureLog4J(log4jFile, logDir);

    }

//    protected void loadHibernateProps() {
//
//    }
    protected String[] loadCmdLineArgs() throws NullPointerException {

        String[] commandLineArgs;

        try {
            commandLineArgs = daemonContext.getArguments();
        } catch (Exception ex) {
            throw new NullPointerException("Failed to read the command line args from the daemon context - " + ex.getMessage());
        }

        System.out.println("--------------------------------------------------------------------");
        System.out.println("                  | -- CmdLine arguments loaded: " + commandLineArgs.length + " -- |                ");
        System.out.println("--------------------------------------------------------------------");

        int x = 1;
        for (String cmdLineArg : commandLineArgs) {
            System.out.println("cmdLineArg. " + x + " : " + cmdLineArg);
            x++;
        }

        System.out.println("--------------------------------------------------------------------");

        return commandLineArgs;
    }

    void addHttpControllers() {

    }

    //re-arrange things - put this method in the right location
    protected void failDaemon(String failureMsg, Exception failureException) {

        System.err.println("FATAL ERROR: Failed to start Daemon: " + failureMsg);

        try {
            daemonContext.getController().fail(failureMsg, failureException);
        } catch (IllegalStateException ise) {
            System.err.println("ERROR: IllegalStateException while failing/shutting down daemon due to previous errors: " + ise.getMessage());
        }

    }

    //re-arrange things - put this method in the right location
    protected void failDaemon(String failureMsg) {

        System.err.println("FATAL ERROR: Failed to start Daemon: " + failureMsg);
        daemonContext.getController().fail(failureMsg);

    }

    //re-arrange things - put this method in the right location
    protected void failDaemon(Exception failureException) {

        System.err.println("FATAL ERROR: Failed to start Daemon: " + failureException.getMessage());
        daemonContext.getController().fail(failureException);

    }

}
