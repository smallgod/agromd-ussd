/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.datamodel.datawrappers;

import com.agromarketday.ussd.datamodel.jaxb.config.Appconfig;
import com.agromarketday.ussd.sharedInterface.RemoteRequest;
import com.agromarketday.ussd.sharedInterface.SharedAppConfigIF;
import java.util.List;



/**
 * We are implementing a wrapper since JAXB classes are ever changing, we dont
 * want a maintenance nightmare
 *
 * @author smallgod
 */
public final class AppConfigWrapper implements SharedAppConfigIF {

    private final Appconfig appConfig;
    private final String xsdFilesDir;
    private final String configsDir;
    private final String logsDir;
    private final String projectDir;
    private final String dsmWebAppDir;
    private final String ussdMenuDir;
    private final String daemonProfile;

    /**
     *
     * @param appConfig
     * @param xsdFilesDir
     * @param configsDir
     * @param logsDir
     * @param projectDir
     * @param dsmWebAppDir
     * @param ussdMenuDir
     * @param daemonProfile
     */
    public AppConfigWrapper(Appconfig appConfig, String xsdFilesDir, String configsDir, String logsDir, String projectDir, String dsmWebAppDir, String ussdMenuDir, String daemonProfile) {

        this.appConfig = appConfig;
        this.xsdFilesDir = xsdFilesDir;
        this.configsDir = configsDir;
        this.logsDir = logsDir;
        this.projectDir = projectDir;
        this.dsmWebAppDir = dsmWebAppDir;
        this.ussdMenuDir = ussdMenuDir;
        this.daemonProfile = daemonProfile;
    }

    /**
     *
     * @param appConfig
     * @param configsDir
     * @param logsDir
     * @param projectDir
     * @param ussdMenuDir
     * @param daemonProfile
     */
    public AppConfigWrapper(Appconfig appConfig, String configsDir, String logsDir, String projectDir, String ussdMenuDir, String daemonProfile) {

        this(appConfig, "", configsDir, logsDir, projectDir, "", ussdMenuDir, daemonProfile);
    }

    /**
     *
     * @param appConfig
     * @param configsDir
     * @param logsDir
     * @param projectDir
     * @param daemonProfile
     */
    public AppConfigWrapper(Appconfig appConfig, String configsDir, String logsDir, String projectDir, String daemonProfile) {

        this(appConfig, "", configsDir, logsDir, projectDir, "", "", daemonProfile);
    }

    @Override
    public int getHttpport() {
        return this.appConfig.getJettyServerConfigs().getHttpport();
    }

    @Override
    public int getHttpsport() {
        return this.appConfig.getJettyServerConfigs().getHttpsport();
    }

    @Override
    public int getAdminport() {
        return this.appConfig.getJettyServerConfigs().getAdminport();
    }

    @Override
    public int getOutputbuffersize() {
        return this.appConfig.getJettyServerConfigs().getOutputbuffersize();
    }

    @Override
    public int getRequestheadersize() {
        return this.appConfig.getJettyServerConfigs().getRequestheadersize();
    }

    @Override
    public int getResponseheadersize() {
        return this.appConfig.getJettyServerConfigs().getResponseheadersize();
    }

    @Override
    public String getContextpath() {
        return this.appConfig.getJettyServerConfigs().getContextpath();
    }

    @Override
    public String getResourceDirAbsPath() {
        return (getProjectDir() + this.appConfig.getJettyServerConfigs().getRelativeresourcedir());
    }

    @Override
    public String getWebxmlfile() {
        return this.appConfig.getJettyServerConfigs().getWebxmlfile();
    }

    @Override
    public String getWebappwarfile() {
        return this.appConfig.getJettyServerConfigs().getWebappwarfile();
    }

    @Override
    public String getKeystorepass() {
        return this.appConfig.getJettyServerConfigs().getKeystorepass();
    }

    @Override
    public String getKeystorepath() {
        return this.appConfig.getJettyServerConfigs().getKeystorepath();
    }

    @Override
    public String getKeystoremanagerpass() {
        return this.appConfig.getJettyServerConfigs().getKeystoremanagerpass();
    }

    @Override
    public String getWelcomefiles() {
        return this.appConfig.getJettyServerConfigs().getWelcomefiles();
    }

    @Override
    public String getLog4JpropsAbsPath() {
        return (getConfigsDir() + this.appConfig.getConfigfiles().getLog4Jprops());
    }

    /**
     * Gets the value of the hibernateprops property.
     *
     * @return possible object is {@link String }
     *
     */
    @Override
    public String getHibernatepropsAbsPath() {
        return (getConfigsDir() + this.appConfig.getConfigfiles().getHibernateprops());
    }

    @Override
    public String getAdDisplayProcessorobName() {
        return this.appConfig.getSchedulers().getDisplayProcessor().getJobname();
    }

    @Override
    public String getAdDisplayProcessorTriggerName() {
        return this.appConfig.getSchedulers().getDisplayProcessor().getTriggername();
    }

    @Override
    public int getAdDisplayProcessorInterval() {
        return this.appConfig.getSchedulers().getDisplayProcessor().getInterval();
    }

    @Override
    public String getAdDisplayProcessorGroupName() {

        return this.appConfig.getSchedulers().getDisplayProcessor().getGroupname();

    }

    @Override
    public String getMidnightCallJobName() {
        return this.appConfig.getSchedulers().getMidnightcall().getJobname();
    }

    @Override
    public String getMidnightCallTriggerName() {
        return this.appConfig.getSchedulers().getMidnightcall().getTriggername();
    }

    @Override
    public int getMidnightCallInterval() {
        return this.appConfig.getSchedulers().getMidnightcall().getInterval();
    }

    @Override
    public String getMidnightCallGroupName() {

        return this.appConfig.getSchedulers().getMidnightcall().getGroupname();

    }

    @Override
    public String getAdPaymentProcessorJobName() {
        return this.appConfig.getSchedulers().getPaymentProcessor().getJobname();
    }

    @Override
    public String getAdPaymentProcessorTriggerName() {
        return this.appConfig.getSchedulers().getPaymentProcessor().getTriggername();
    }

    @Override
    public int getAdPaymentProcessorInterval() {
        return this.appConfig.getSchedulers().getPaymentProcessor().getInterval();
    }

    @Override
    public String getAdPaymentProcessorGroupName() {
        return this.appConfig.getSchedulers().getPaymentProcessor().getGroupname();
    }

    @Override
    public String getAdCampaignProcessorJobName() {
        return this.appConfig.getSchedulers().getCampaignProcessor().getJobname();
    }

    @Override
    public String getAdCampaignProcessorTriggerName() {
        return this.appConfig.getSchedulers().getCampaignProcessor().getTriggername();
    }

    @Override
    public int getAdCampaignProcessorInterval() {
        return this.appConfig.getSchedulers().getCampaignProcessor().getInterval();
    }

    @Override
    public String getAdCampaignProcessorGroupName() {
        return this.appConfig.getSchedulers().getCampaignProcessor().getGroupname();
    }

    @Override
    public String getLogsDir() {
        return logsDir;
    }

    @Override
    public String getXsdFilesDir() {
        return this.xsdFilesDir;
    }

    @Override
    public String getConfigsDir() {
        return configsDir;
    }

    @Override
    public String getProjectDir() {
        return projectDir;
    }

    @Override
    public String getDsmWebAppDir() {
        return dsmWebAppDir;
    }

    @Override
    public String getDaemonProfile() {
        return daemonProfile;
    }

    @Override
    public int getConnTimeout() {
        return appConfig.getConntimeout();
    }

    @Override
    public int getReadTimeout() {
        return appConfig.getReadtimeout();
    }

    @Override
    public int getMaxConnections() {
        return appConfig.getMaxtotalconnections();
    }

    @Override
    public int getConnPerRoute() {
        return appConfig.getConnectionsperroute();
    }

    @Override
    public List<String> getAllowedIps() {
        return (appConfig.getAllowedips().getIp());
    }

    @Override
    public RemoteRequest getAdDisplayUnit() {
        return new RemoteUnitWrapper(appConfig.getRemoteunits().getAddisplay());
    }

    @Override
    public RemoteRequest getAdCentralUnit() {
        return new RemoteUnitWrapper(appConfig.getRemoteunits().getAdcentral());
    }

    @Override
    public RemoteRequest getAdDbManagerUnit() {
        return new RemoteUnitWrapper(appConfig.getRemoteunits().getAddbmanager());
    }

    @Override
    public String getUssdMenuDir() {
        return ussdMenuDir;
    }

    @Override
    public RemoteRequest getDSMBridgeUnit() {
        return new RemoteUnitWrapper(appConfig.getRemoteunits().getDsmbridge());
    }

}
