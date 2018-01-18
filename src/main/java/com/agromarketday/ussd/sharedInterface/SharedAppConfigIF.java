/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.sharedInterface;

import java.util.List;

/**
 *
 * @author smallgod
 */
public interface SharedAppConfigIF {

    public int getHttpport();

    /**
     * Gets the value of the httpsport property.
     *
     * @return httport
     */
    public int getHttpsport();

    /**
     * Gets the value of the adminport property.
     *
     * @return
     */
    public int getAdminport();

    /**
     * Gets the value of the outputbuffersize property.
     *
     * @return
     */
    public int getOutputbuffersize();

    /**
     * Gets the value of the requestheadersize property.
     *
     * @return
     */
    public int getRequestheadersize();

    /**
     * Gets the value of the responseheadersize property.
     *
     * @return
     */
    public int getResponseheadersize();

    /**
     * Gets the value of the contextpath property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getContextpath();

    /**
     * Gets the value of the relativeresourcedir property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getResourceDirAbsPath();

    /**
     * Gets the value of the webxmlfile property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getWebxmlfile();

    /**
     * Gets the value of the webappwarfile property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getWebappwarfile();

    /**
     * Gets the value of the keystorepass property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getKeystorepass();

    /**
     * Gets the value of the keystorepath property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getKeystorepath();

    /**
     * Gets the value of the keystoremanagerpass property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getKeystoremanagerpass();

    /**
     * Gets the value of the welcomefiles property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getWelcomefiles();

    /**
     * Gets the value of the centralServerJsonUrl property.
     *
     * @return possible object is {@link String }
     *
     */
    //public String getCentralServerJsonUrl();
    /**
     * Gets the value of the centralServerXmlUrl property.
     *
     * @return possible object is {@link String }
     *
     */
    //public String getCentralServerXmlUrl();
    /**
     * Gets the value of the centralServerJsonUrl property.
     *
     * @return possible object is {@link String }
     *
     */
    //public String getAdDisplayUnitJsonUrl();
    /**
     * Gets the value of the centralServerXmlUrl property.
     *
     * @return possible object is {@link String }
     *
     */
    //public String getAdDisplayUnitXmlUrl();
    /**
     * Gets the value of the log4jprops property
     *
     * @return possible object is {@link String }
     */
    public String getLog4JpropsAbsPath();

    /**
     * Gets the value of the hibernateprops property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getHibernatepropsAbsPath();

    /**
     * AdPayment Job values
     * @return 
     */
    public String getAdPaymentProcessorJobName();

    public String getAdPaymentProcessorTriggerName();

    public int getAdPaymentProcessorInterval();

    public String getAdPaymentProcessorGroupName();
    
    
    /**
     * AdPayment Job values
     * @return 
     */
    public String getAdCampaignProcessorJobName();

    public String getAdCampaignProcessorTriggerName();

    public int getAdCampaignProcessorInterval();

    public String getAdCampaignProcessorGroupName();
    
    /**
     * Ad Display values
     * @return 
     */
    public String getAdDisplayProcessorobName();

    public String getAdDisplayProcessorTriggerName();

    public int getAdDisplayProcessorInterval();

    public String getAdDisplayProcessorGroupName();

    /**
     * Midnight fetcher values
     * @return 
     */
    public String getMidnightCallJobName();

    public String getMidnightCallTriggerName();

    public int getMidnightCallInterval();

    public String getMidnightCallGroupName();

    public String getXsdFilesDir();

    public String getLogsDir();

    public String getConfigsDir();

    public String getProjectDir();

    public String getDsmWebAppDir();

    public String getUssdMenuDir();

    public String getDaemonProfile();

    public int getConnTimeout();

    public int getReadTimeout();

    public int getMaxConnections();

    public int getConnPerRoute();

    public List<String> getAllowedIps();

    public RemoteRequest getAdDisplayUnit();

    public RemoteRequest getAdCentralUnit();

    public RemoteRequest getAdDbManagerUnit();

    public RemoteRequest getDSMBridgeUnit();


}
