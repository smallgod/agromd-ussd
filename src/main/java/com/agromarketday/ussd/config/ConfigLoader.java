/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.config;

import com.agromarketday.ussd.constant.NamedConstants;
import com.agromarketday.ussd.exception.MyCustomException;
import com.agromarketday.ussd.sharedInterface.RemoteRequest;
import com.agromarketday.ussd.sharedInterface.SharedAppConfigIF;
import com.agromarketday.ussd.util.FileUtilities;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author smallgod
 */
public class ConfigLoader {

    private final SharedAppConfigIF appConfig;

    public ConfigLoader(SharedAppConfigIF appConfig) {

        this.appConfig = appConfig;
    }

    /**
     * Get config parameters for the Ad Scheduler/Display Job
     *
     * @return
     */
    public JobsConfig getAdDisplayJobConfig() {

        String triggerName = appConfig.getAdDisplayProcessorTriggerName();
        String jobName = appConfig.getAdDisplayProcessorobName();
        String groupName = appConfig.getAdDisplayProcessorGroupName();
        int repeatInterval = appConfig.getAdDisplayProcessorInterval();

        RemoteUnitConfig remoteUnitConfig = getRemoteUnitConfig();
        JobsConfig jobConfig = new JobsConfig(triggerName, jobName, groupName, repeatInterval, remoteUnitConfig);

        return jobConfig;
    }

    /**
     * Get Job configs for Ad payment processor job
     *
     * @return
     */
    public JobsConfig getAdPaymentJobConfig() {

        String triggerName = appConfig.getAdPaymentProcessorTriggerName();
        String jobName = appConfig.getAdPaymentProcessorJobName();
        String groupName = appConfig.getAdPaymentProcessorGroupName();
        int repeatInterval = appConfig.getAdPaymentProcessorInterval();

        RemoteUnitConfig remoteUnitConfig = getRemoteUnitConfig();
        JobsConfig jobConfig = new JobsConfig(triggerName, jobName, groupName, repeatInterval, remoteUnitConfig);

        return jobConfig;
    }

    /**
     * Get Job configs for Ad campaign processor job
     *
     * @return
     */
    public JobsConfig getAdCampaignProcessorJobConfig() {

        String triggerName = appConfig.getAdCampaignProcessorTriggerName();
        String jobName = appConfig.getAdCampaignProcessorJobName();
        String groupName = appConfig.getAdCampaignProcessorGroupName();
        int repeatInterval = appConfig.getAdCampaignProcessorInterval();

        RemoteUnitConfig remoteUnitConfig = getRemoteUnitConfig();
        JobsConfig jobConfig = new JobsConfig(triggerName, jobName, groupName, repeatInterval, remoteUnitConfig);

        return jobConfig;
    }

    public JobsConfig getMidnightCallJobConfig() {

        String triggerName = appConfig.getMidnightCallTriggerName();
        String jobName = appConfig.getMidnightCallJobName();
        String groupName = appConfig.getMidnightCallGroupName();
        int repeatInterval = appConfig.getMidnightCallInterval();

        RemoteUnitConfig remoteUnitConfig = getRemoteUnitConfig();
        JobsConfig jobConfig = new JobsConfig(triggerName, jobName, groupName, repeatInterval, remoteUnitConfig);

        return jobConfig;
    }

    public RemoteUnitConfig getRemoteUnitConfig() {

        RemoteRequest dsmBridgeUnit = appConfig.getDSMBridgeUnit();
        RemoteRequest centralUnit = appConfig.getAdCentralUnit();
        RemoteRequest adDisplayUnt = appConfig.getAdDisplayUnit();
        RemoteRequest adDbManagerUnit = appConfig.getAdDbManagerUnit();

        Map<String, RemoteRequest> remoteUnits = new HashMap<>();

        remoteUnits.put(NamedConstants.DSM_UNIT_REQUEST, dsmBridgeUnit);
        remoteUnits.put(NamedConstants.CENTRAL_UNIT_REQUEST, centralUnit);
        remoteUnits.put(NamedConstants.ADDISPLAY_UNIT_REQUEST, adDisplayUnt);
        remoteUnits.put(NamedConstants.ADDBManager_UNIT_REQUEST, adDbManagerUnit);

        RemoteUnitConfig remoteUnitConfig = new RemoteUnitConfig(remoteUnits);
        return remoteUnitConfig;

    }

    /**
     * Get the configuration parameters necessary for an HTTP Client Pool
     * connection
     *
     * @return
     */
    public HttpClientPoolConfig getHttpClientPoolConfig() {

        int readTimeout = appConfig.getReadTimeout();
        int connTimeout = appConfig.getConnTimeout();
        int connPerRoute = appConfig.getConnPerRoute();
        int maxConnections = appConfig.getMaxConnections();

        HttpClientPoolConfig clientPoolConfig = new HttpClientPoolConfig(readTimeout, connTimeout, connPerRoute, maxConnections);

        return clientPoolConfig;

    }

    /**
     * Get the configuration parameters necessary for a JETTY server setup
     *
     * @return
     */
    public JettyServerConfig getJettyServerConfig() {

        String contextPath = appConfig.getContextpath();
        String resourceBase = appConfig.getResourceDirAbsPath();
        String webDescriptor = resourceBase + appConfig.getWebxmlfile();
        int httpPort = appConfig.getHttpport();

        JettyServerConfig serverConfig = new JettyServerConfig(contextPath, resourceBase, webDescriptor, httpPort);

        return serverConfig;
    }

    /**
     * Get the Custom Hibernate configuration parameters
     *
     * @return
     */
    public HibernateConfig getHibernateConfig() {

        String hibernateFilePath = appConfig.getHibernatepropsAbsPath();

        HibernateConfig config = new HibernateConfig(hibernateFilePath);

        return config;
    }

    /**
     * Get the config parameters for the Database setup
     *
     * @return
     */
    public DatabaseConfig getDatabaseConfig() {

        RemoteUnitConfig remoteUnitConfig = getRemoteUnitConfig();

        RemoteRequest dbManagerUnit = remoteUnitConfig.getAdDbManagerRemoteUnit();

        DatabaseConfig databaseConfig = new DatabaseConfig(dbManagerUnit);

        return databaseConfig;
    }

    /**
     * Get config parameters for the USSD menu Directory
     *
     * @return
     * @throws com.agromarketday.ussd.exception.MyCustomException
     */
    public UssdMenuConfig getUssdMenuDirConfig() throws MyCustomException {
        String ussdMenuDir = appConfig.getUssdMenuDir();

        UssdMenuConfig menuDirConfig = new UssdMenuConfig(ussdMenuDir);
        return menuDirConfig;
    }

    /**
     * Sets the work root folder
     *
     * @param path the ROOT folder for the app
     */
    private String setWorkRootPath(String path) throws IllegalArgumentException, Exception {

        //String path = rootFolder + File.separatorChar + "gnamp_work";
        File dir = new File(path);
        path = dir.getAbsolutePath();

        if ((dir.exists()) && (!dir.isDirectory()) && (!dir.delete())) {
            throw new IllegalArgumentException("path is not directory, delete file error: " + path);
        }
        if (!dir.exists()) {

            if (!FileUtilities.createDirectory(path)) {
                throw new Exception("Failed to create dsm work root directory");
            }
        }
        String workRoot = path + File.separatorChar;

        return workRoot;
    }

    /**
     * Get the config parameters for the AdDisplayProcessor Unit
     *
     * @return
     *
     * public AdDisplayProcessorConfig getAdDisplayProcessorConfig() {
     *
     * String adFetchJobTriggerName = appConfig.getAdFetcherTriggerName();
     * String adFetchJobName = appConfig.getAdFetcherJobName(); String
     * adFetchJobGroupName = appConfig.getAdFetcherGroupName();
     *
     * AdDisplayProcessorConfig adDisplayProcConfig = new
     * AdDisplayProcessorConfig(adFetchJobTriggerName, adFetchJobName,
     * adFetchJobGroupName);
     *
     * return adDisplayProcConfig; }
     *
     */
}
